#!/usr/bin/env python3
"""Build a narrow, reproducible compatibility edition of the official Angelica release."""
import argparse
import difflib
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import subprocess
import sys
import urllib.request
import zipfile

ROOT = Path(__file__).resolve().parent
VERSION = "2.2.46-zalith.2"
CLASS = "com/gtnewhorizons/angelica/glsm/backend/Lwjgl2GLRenderBackend.class"
REDIRECTOR_CLASS = "com/gtnewhorizons/angelica/glsm/redirect/GLSMRedirector.class"
HELPER = "com/gtnewhorizons/angelica/glsm/backend/ZalithShaderQueries"
SOURCE = "glsm/src/main/java/com/gtnewhorizons/angelica/glsm/backend/Lwjgl2GLRenderBackend.java"
REDIRECTOR_SOURCE = "glsm/src/main/java/com/gtnewhorizons/angelica/glsm/redirect/GLSMRedirector.java"
STAMP = (2026, 11, 21, 0, 0, 0)


def sha256(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def run(command, label):
    result = subprocess.run([str(x) for x in command], cwd=ROOT, stdout=subprocess.PIPE,
                            stderr=subprocess.STDOUT, encoding="utf-8", errors="replace")
    (ROOT / "build" / (label + ".log")).write_text(result.stdout, encoding="utf-8")
    if result.returncode:
        raise RuntimeError(label + " failed:\n" + result.stdout[-9000:])
    return result.stdout.strip()


def put(archive, name, contents):
    info = zipfile.ZipInfo(name, STAMP)
    info.compress_type = zipfile.ZIP_DEFLATED
    info.external_attr = 0o100644 << 16
    archive.writestr(info, contents)


def dependencies(offline):
    entries = json.loads((ROOT / "dependencies.lock.json").read_text(encoding="utf-8"))
    paths = {}
    for entry in entries:
        path = ROOT / entry["path"]
        if not path.exists():
            if offline:
                raise RuntimeError("Missing cached dependency: " + entry["path"])
            path.parent.mkdir(parents=True, exist_ok=True)
            request = urllib.request.Request(entry["url"], headers={"User-Agent": "Angelica-Zalith-build"})
            with urllib.request.urlopen(request, timeout=120) as response:
                data = response.read()
            if hashlib.sha256(data).hexdigest() != entry["sha256"]:
                raise RuntimeError("Downloaded dependency checksum mismatch: " + entry["id"])
            path.write_bytes(data)
        if sha256(path) != entry["sha256"]:
            raise RuntimeError("Cached dependency checksum mismatch: " + entry["id"])
        paths[entry["id"]] = path
    return paths


def patch_source(source):
    for kind in ("Uniform", "Attrib"):
        old = "return GL20.glGetActive" + kind + "(program, index, maxLength, sizeType);"
        new = "return ZalithShaderQueries.getActive" + kind + "(program, index, maxLength, sizeType);"
        if source.count(old) != 1:
            raise RuntimeError("Unexpected upstream source for " + kind)
        source = source.replace(old, new)
    return source


def patch_redirector_source(source):
    old = '        "me.eigenraven.lwjgl3ify"\n'
    new = '        "me.eigenraven.lwjgl3ify",\n        "org.taumc.celeritas.lwjgl.lwjgl3.LWJGL3Service"\n'
    if source.count(old) != 1:
        raise RuntimeError("Unexpected upstream redirector exclusion list")
    return source.replace(old, new)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--java-home", type=Path)
    parser.add_argument("--offline", action="store_true")
    parser.add_argument("--skip-native", action="store_true", help="Build and inspect only; mark native tests unrun")
    args = parser.parse_args()
    (ROOT / "build").mkdir(exist_ok=True)
    (ROOT / "dist").mkdir(exist_ok=True)
    local_jdk = ROOT.parent / "industrialMOD/.toolchain/jdk8/jdk8u504-b01"
    java_home = args.java_home or (Path(os.environ["JAVA_HOME"]) if os.environ.get("JAVA_HOME") else local_jdk)
    suffix = ".exe" if os.name == "nt" else ""
    java, javac = java_home / ("bin/java" + suffix), java_home / ("bin/javac" + suffix)
    if not javac.exists():
        raise RuntimeError("Set --java-home to a Java 8 JDK")
    if 'version "1.8.' not in run([java, "-version"], "java-version"):
        raise RuntimeError("Use a Java 8 JDK to preserve the mobile runtime API")
    deps = dependencies(args.offline)
    classes, tools = ROOT / "build/classes", ROOT / "build/tools"
    classes.mkdir(exist_ok=True)
    tools.mkdir(exist_ok=True)
    asm = os.pathsep.join(str(deps[key]) for key in ("asm", "asm-tree"))
    tool_sources = sorted((ROOT / "tools").glob("*.java"))
    run([javac, "-encoding", "UTF-8", "-g", "-cp", asm, "-d", tools, *tool_sources], "compile-tools")
    runtime_cp = os.pathsep.join(str(deps[key]) for key in ("zalith2-lwjgl", "zalith2-lwjgl-opengl"))
    helper_source = ROOT / ("src/main/java/" + HELPER + ".java")
    run([javac, "-encoding", "UTF-8", "-g", "-cp", runtime_cp, "-d", classes, helper_source], "compile-helper")
    patch_cp = os.pathsep.join((str(tools), asm))
    replacements, bytecode_results, routing_results = {}, [], []
    with zipfile.ZipFile(deps["angelica"]) as original:
        if original.testzip() is not None:
            raise RuntimeError("Invalid original release archive")
        targets = [name for name in original.namelist() if name == CLASS or name.endswith("/" + CLASS)]
        if targets != ["META-INF/versions/17/" + CLASS, CLASS] and set(targets) != {CLASS, "META-INF/versions/17/" + CLASS}:
            raise RuntimeError("Unexpected multi-release backends: " + repr(targets))
        for index, name in enumerate(targets):
            before, after = ROOT / ("build/backend-%d-before.class" % index), ROOT / ("build/backend-%d-after.class" % index)
            before.write_bytes(original.read(name))
            run([java, "-cp", patch_cp, "PatchBackend", before, after], "patch-%d" % index)
            result = run([java, "-cp", patch_cp, "VerifyBackend", before, after], "verify-%d" % index)
            bytecode_results.append({"entry": name, "result": result})
            replacements[name] = after.read_bytes()
            print(result + " (" + ("Java 17+" if index == 0 else "Java 8") + ")")
        redirectors = [name for name in original.namelist() if name == REDIRECTOR_CLASS or name.endswith("/" + REDIRECTOR_CLASS)]
        if set(redirectors) != {REDIRECTOR_CLASS, "META-INF/versions/17/" + REDIRECTOR_CLASS}:
            raise RuntimeError("Unexpected multi-release redirectors: " + repr(redirectors))
        for index, name in enumerate(redirectors):
            before = ROOT / ("build/redirector-%d-before.class" % index)
            after = ROOT / ("build/redirector-%d-after.class" % index)
            before.write_bytes(original.read(name))
            result = run([java, "-cp", patch_cp, "PatchRedirectExclusions", before, after], "patch-redirector-%d" % index)
            routing_results.append({"entry": name, "result": result})
            replacements[name] = after.read_bytes()
            print(result)
        runtime = ROOT / ("dist/angelica-" + VERSION + ".jar")
        with zipfile.ZipFile(runtime, "w") as output:
            for entry in original.infolist():
                if re.fullmatch(r"META-INF/[^/]+\.(SF|RSA|DSA|EC)", entry.filename, re.I):
                    raise RuntimeError("Upstream is signed; do not strip signatures silently")
                output.writestr(entry, replacements.get(entry.filename, original.read(entry.filename)))
            for path in sorted(classes.rglob("*.class")):
                put(output, path.relative_to(classes).as_posix(), path.read_bytes())
            put(output, "META-INF/angelica-zalith.properties",
                ("edition=unofficial-zalith-compatibility\nversion=" + VERSION + "\nupstream=2.2.46\n"
                 + "upstream.sha256=" + sha256(deps["angelica"]) + "\nmaintainer=Zahin\n").encode())

    with zipfile.ZipFile(deps["angelica"]) as base, zipfile.ZipFile(runtime) as built:
        unchanged = [name for name in base.namelist() if name not in replacements]
        for name in unchanged:
            if base.read(name) != built.read(name):
                raise AssertionError("Unrelated release entry changed: " + name)
        for name in base.namelist():
            if name.endswith(".class"):
                if int.from_bytes(base.read(name)[6:8], "big") != int.from_bytes(built.read(name)[6:8], "big"):
                    raise AssertionError("Class Java version changed: " + name)
        for path in classes.rglob("*.class"):
            if int.from_bytes(path.read_bytes()[6:8], "big") != 52:
                raise AssertionError("Compatibility helper is not Java 8")
    print("PASS: %d original release entries are byte-identical; all original configuration retained" % len(unchanged))

    native_results = []
    if not args.skip_native:
        if sys.platform != "win32":
            raise RuntimeError("Native fixtures currently support Windows; use --skip-native elsewhere")
        for edition, version in (("zalith1", "3.3.6"), ("zalith2", "3.3.3")):
            natives = ROOT / ("build/natives-" + version)
            natives.mkdir(exist_ok=True)
            for module in ("lwjgl", "lwjgl-opengl", "lwjgl-glfw"):
                with zipfile.ZipFile(deps["native-" + version + "-" + module]) as archive:
                    for entry in archive.infolist():
                        if entry.filename.endswith(".dll"):
                            (natives / Path(entry.filename).name).write_bytes(archive.read(entry))
            test_classes = ROOT / ("build/tests-" + edition)
            test_classes.mkdir(exist_ok=True)
            # Read the helper from the shipped JAR, not the compiler output directory.
            test_deps = [deps[edition + "-lwjgl"], deps[edition + "-lwjgl-opengl"], deps["glfw-" + version],
                         deps[edition + "-lwjglx"], deps["asm"], deps["asm-tree"]]
            test_deps += [deps[key] for key in ("gtnhlib", "jvmdowngrader", "guava", "log4j-api", "log4j-core")]
            cp = os.pathsep.join(str(path) for path in [runtime, *test_deps])
            run([javac, "-encoding", "UTF-8", "-g", "-cp", cp, "-d", test_classes,
                 *sorted((ROOT / "tests").glob("*.java"))], "compile-tests-" + edition)
            cp = str(test_classes) + os.pathsep + cp
            original_cp = os.pathsep.join(str(path) for path in [test_classes, deps["angelica"], *test_deps])
            control = run([java, "-Xverify:all", "-cp", original_cp, "RedirectRoutingChecks", "original"], "routing-control-" + edition)
            routing = run([java, "-Xverify:all", "-cp", cp, "RedirectRoutingChecks", "patched"], "routing-fixed-" + edition)
            result = run([java, "-Xverify:all", "-Dorg.lwjgl.librarypath=" + str(natives),
                          "-cp", cp, "NativeShaderQueryChecks", edition], "native-" + edition)
            sync = run([java, "-Xverify:all", "-Dorg.lwjgl.librarypath=" + str(natives),
                        "-cp", cp, "NativeSyncRoutingChecks", edition], "native-sync-" + edition)
            print(control)
            print(routing)
            print(result)
            print(sync)
            native_results.append({"launcher": edition, "lwjgl": version, "result": result,
                                   "routing_control": control, "routing_fixed": routing, "native_sync": sync})

    # Include the full upstream project with the source-level equivalents of the binary patch.
    source_bundle = ROOT / ("dist/angelica-" + VERSION + "-sources.zip")
    with zipfile.ZipFile(deps["angelica-source-zip"]) as source_zip, zipfile.ZipFile(source_bundle, "w") as output:
        original_source = next(name for name in source_zip.namelist() if name.endswith("/" + SOURCE))
        old = source_zip.read(original_source).decode("utf-8")
        new = patch_source(old)
        patch = "".join(difflib.unified_diff(old.splitlines(True), new.splitlines(True),
                    fromfile="a/" + SOURCE, tofile="b/" + SOURCE))
        (ROOT / "patches/shader-query-bridge.patch").write_text(patch, encoding="utf-8")
        prefix = original_source[:-len(SOURCE)]
        original_redirector_source = prefix + REDIRECTOR_SOURCE
        old_redirector = source_zip.read(original_redirector_source).decode("utf-8")
        new_redirector = patch_redirector_source(old_redirector)
        redirector_patch = "".join(difflib.unified_diff(old_redirector.splitlines(True), new_redirector.splitlines(True),
                                  fromfile="a/" + REDIRECTOR_SOURCE, tofile="b/" + REDIRECTOR_SOURCE))
        (ROOT / "patches/native-service-routing.patch").write_text(redirector_patch, encoding="utf-8")
        for entry in source_zip.infolist():
            if not entry.is_dir():
                path = "upstream/" + entry.filename
                data = source_zip.read(entry)
                if entry.filename == original_source:
                    data = new.encode("utf-8")
                elif entry.filename == original_redirector_source:
                    data = new_redirector.encode("utf-8")
                put(output, path, data)
        put(output, "upstream/" + prefix + "glsm/src/main/java/" + HELPER + ".java", helper_source.read_bytes())
        for path in sorted(ROOT.rglob("*")):
            if not path.is_file():
                continue
            rel = path.relative_to(ROOT)
            if rel.parts[0] in {"upstream", "build", "dist", "__pycache__"}:
                continue
            if path.suffix == ".pyc":
                continue
            put(output, "compatibility/" + rel.as_posix(), path.read_bytes())

    report = {"build": VERSION, "upstream_sha256": sha256(deps["angelica"]),
              "modified_original_entries": sorted(replacements), "unchanged_original_entries": len(unchanged),
              "backend_verification": bytecode_results, "redirector_verification": routing_results, "native_tests": native_results,
              "android_device_tested": False, "performance_features_disabled": [],
              "artifacts": [{"file": path.name, "bytes": path.stat().st_size, "sha256": sha256(path)}
                            for path in (runtime, source_bundle)]}
    (ROOT / "dist/verification.json").write_text(json.dumps(report, indent=2) + "\n", encoding="utf-8")
    (ROOT / "dist/SHA256SUMS.txt").write_text("".join(item["sha256"] + "  " + item["file"] + "\n"
                                            for item in report["artifacts"]), encoding="utf-8")
    print("Built: " + str(runtime))
    print("Android phone testing is still required; no performance features were disabled.")


if __name__ == "__main__":
    main()
