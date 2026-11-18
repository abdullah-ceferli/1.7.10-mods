"""Write a complete, source-matched Markdown inventory without modifying port sources."""
from pathlib import Path
from collections import Counter, defaultdict
import hashlib, html, json, re, zipfile

ROOT = Path(__file__).resolve().parents[1]
CAUSES = {
 'R01': ('Local difficulty and mob spawning', 'DifficultyInstance and modern spawning callbacks do not match the native 1.7.10 API.', 'Port local difficulty calculation and spawn initialization. The global difficulty enum alone does not preserve local difficulty behavior.'),
 'R02': ('Hands, equipment and item-use results', 'Modern hand/equipment enums and action-result contracts differ from the single-held-item and slot-based native API.', 'Map supported hand/slot behavior and item-use outcomes explicitly; preserve original interaction and equipment semantics.'),
 'R03': ('Movement controllers and movement modes', 'Modern movement modes, controller actions and gravity controls differ from the old movement API.', 'Implement the required controller state transitions and movement behavior, then validate collision and motion on a real server.'),
 'R04': ('Retaliation AI constructor and recruitment', 'The native EntityAIHurtByTarget constructor lacks the modern Class... helper-exclusion argument.', 'Preserve exclusion filtering, recruitment bounds and tame-owner behavior in a real AI adapter. Dropping the third argument loses behavior.'),
 'R05': ('AI, path navigation and entity lifecycle', 'Entity AI classes, navigation inheritance and lifecycle methods have changed or moved.', 'Port the actual targeting, navigation and lifecycle behavior. Climbing/flying/swimming navigation requires more than an empty renamed class.'),
 'R06': ('Potion arguments and numeric effect APIs', 'The native potion API uses numeric IDs at constructors and several effect methods, while modern calls often pass Potion objects. The exact receiver and operation determine the fix.', 'Constructor conversion and server particle visibility are tested. Inspect the remaining named native operation (for example removePotionEffect), adapt its numeric ID contract, and preserve native gameplay and the implemented visibility lifecycle.'),
 'R07': ('Potion catalogs, types and brewing', 'Modern potion catalogs, PotionType and brewing registrations differ from the old effect-ID system.', 'Map each vanilla effect and implement potion-type/brewing behavior against native registries without losing recipes or effect data.'),
 'R08': ('Client rendering, particles and model APIs', 'The 1.12 rendering/particle pipeline has incompatible classes, generic renderer contracts and member signatures.', 'Port particles, vertex submission, models and render layers to the 1.7 client. The verified shared GL-state adapter does not complete the original renderers.'),
 'R09': ('Sound catalogs, categories and packets', 'Modern sound constants, category packages, packets and playback signatures do not line up with the native string-based API.', 'Use the preserved sound identifiers and native playback/networking at each boundary; verify category and positional behavior. Original custom sound callbacks are already partly converted.'),
 'R10': ('Block states and specialized block APIs', 'Modern block rendering, specialized subclasses and state-aware callback contracts differ from native metadata blocks.', 'Complete vanilla state codecs and specialized inheritance/callback adapters. The existing state graph and metadata bridge cover only part of this system.'),
 'R11': ('Biomes, world generation and structures', 'Modern biome/world-generation types, chunk builders and structure-template APIs differ from the old native interfaces.', 'Port biome registration, terrain/structure generation and world queries while preserving the original data and generation rules.'),
 'R12': ('Forge registries and event subscription', 'Modern Forge registry-entry/builders and automatic subscriber contracts are absent or incompatible in this target.', 'Finish explicit native lifecycle registration and event hookup. Existing item/block registration work does not cover every remaining registry or subscriber.'),
 'R13': ('Advancements and progression', 'Minecraft 1.7.10 has no native advancement system matching the imported 1.12 classes.', 'Port triggers, persistent completion data and required feedback. Removing triggers or adding no-op stubs would lose progression behavior.'),
 'R14': ('Capabilities and inventory providers', 'Modern Forge capabilities and item-handler contracts do not exist in the same form in Forge 1.7.10.', 'Implement the actual provider, inventory, serialization and interaction behavior using native hooks, including save/reload and multiplayer validation.'),
 'R15': ('Boss bars and tracking', 'The modern tracked BossInfo/BossInfoServer model differs from the old global BossStatus display.', 'Port server tracking, progress/state packets and the client HUD. A single global bar or removal of the bar does not preserve the original system.'),
 'R16': ('ItemStack and modern item APIs', 'Modern empty-stack, count, property-override and cooldown contracts differ from native nullable stack/item conventions.', 'Adapt each stack/item boundary, including empty/null handling, quantities and use behavior; verify the receiver type before rewriting.'),
 'R17': ('Native Vec3 and modern Vec3d boundary', 'A native API returns Vec3 where converted code expects the existing Vec3d compatibility type.', 'Convert vectors at the native boundary. A global Vec3d-to-Vec3 rename would undo already-tested modern vector semantics.'),
 'R18': ('Raw collections and recovered generic types', 'Native APIs or recovered declarations expose raw/Object types where this expression expects a specific generic type.', 'Identify the actual collection element type and use a typed boundary or correct declaration. Many entity queries are already migrated; inspect remaining receivers.'),
 'R19': ('Integer-to-byte narrowing', 'The expression supplies an int where Java requires a byte; the compiler cannot assume the value fits.', 'Inspect the field/key/metadata type and original bytecode before narrowing. Blind casts can truncate values or conceal an incorrectly typed expression.'),
 'R20': ('Fall-damage multiplier', 'The native fall method takes one distance argument, whereas this call also passes a damage multiplier.', 'Preserve the modern damage calculation and rounding. Dropping the multiplier or simply scaling distance changes damage thresholds.'),
 'R21': ('Entity position method mismatch', 'The modern zero-argument block-position call does not match native getPosition(float), which is an interpolated vector method.', 'Use the existing EntityQueries.position conversion with floor(x), floor(y + 0.5), floor(z), checking the exact receiver first.'),
 'R22': ('Additional math helpers', 'This named modern math helper is unavailable on the target math class.', 'Add or route to a behavior-matched implementation and verify edge cases and precision against the original behavior.'),
 'R23': ('GUI, tooltips and chat contracts', 'Client GUI/tooltips/chat method signatures and supporting interfaces differ across these Minecraft versions.', 'Adapt the real argument and text/rendering contracts. Preserve formatting and behavior rather than discarding extra parameters indiscriminately.'),
 'R24': ('Tile entities, NBT and saved world data', 'The named persistence/tile contract is unresolved; it may be a package/signature change or a cascade from an unresolved superclass.', 'Check the declared owner and superclass, then port coordinates, dirty marking, ticking, NBT and update packets as needed. Do not assume markDirty itself is universally absent.'),
 'R25': ('Forge event contracts', 'Modern event classes, payloads or lifecycle stages differ from those exposed by the target Forge.', 'Map each event to the appropriate native hook and preserve ordering, cancellation and client/server side behavior.'),
 'R26': ('Entity attributes and combat', 'Modern attribute/combat members are unavailable or have changed contracts in the native target.', 'Port the required attribute and damage behavior, including modifiers and synchronization; do not replace missing attributes with unrelated constants.'),
 'R27': ('Multipart entities', 'The multipart entity type or its damage/ownership contract differs from the native target.', 'Port entity-part ownership, collision, damage forwarding and required tracking behavior.'),
 'R28': ('Area-effect clouds', 'The modern area-effect-cloud entity is not provided by this native version.', 'Implement its lifetime, radius, effect application and synchronization behavior or a validated equivalent.'),
 'R29': ('Fluid integration', 'The diagnostic occurs in the fluid package, whose original fluid/block integration still needs conversion; this classification alone is not an exact diagnosis.', 'Inspect the reported symbol and receiver and port its real fluid/container/rendering boundary. Do not infer a fix from package location alone.'),
 'R30': ('Inheritance, overrides and visibility contracts', 'The reported override, generic base-class or visibility contract does not match the target superclass/interface.', 'Resolve the actual target signature and superclass first; some errors are cascades from another missing type. Preserve the callback behavior.'),
 'R31': ('Recovered Java syntax, casts and overloads', 'Javac rejected the syntax, cast or overload typing of this expression; recovered source and earlier API mismatches can both contribute.', 'Compare this exact converted expression with the preserved reference and original bytecode. Fix the verified expression without broad blind substitutions.'),
 'R99': ('Exact root cause still needs individual review', 'The available diagnostic is insufficient for a trustworthy specific root-cause classification. Missing symbols can also cascade from an unresolved parent or incompatible receiver.', 'Inspect the exact source line, symbol/location, owner, inheritance and native API. Resolve shared root causes, recompile, and reassess this diagnostic; no specific replacement is asserted here.'),
}

TYPE_GROUPS = {
 'R01': 'DifficultyInstance IEntityLivingData',
 'R02': 'EnumHand EnumHandSide EntityEquipmentSlot ActionResult EnumActionResult',
 'R03': 'MoverType',
 'R05': 'EntityAIAttackMelee PathNavigateGround PathNavigateFlying PathNavigateSwimmer PathNavigateClimber PathNodeType EntitySelectors SpawnPlacementType',
 'R07': 'MobEffects PotionType PotionTypes IBrewingRecipe',
 'R08': 'BufferBuilder Particle ParticleManager RenderLivingBase LayerRenderer IBakedModel TextureAtlasSprite VertexFormat DefaultVertexFormats IRenderFactory',
 'R09': 'SoundEvents SoundCategory SPacketSoundEffect SoundEvent',
 'R10': 'SoundType BlockRenderLayer EnumBlockRenderType EnumPushReaction BlockEndGateway BlockHorizontal',
 'R11': 'Biome Biomes ChunkPrimer Template IChunkGenerator',
 'R12': 'IForgeRegistryEntry EventBusSubscriber ForgeRegistries EntityEntry EntityEntryBuilder RegistryEvent',
 'R13': 'Advancement CriteriaTriggers ICriterionInstance AbstractCriterionInstance ICriterionTrigger',
 'R14': 'Capability IItemHandler ICapabilityProvider ICapabilitySerializable CapabilityItemHandler',
 'R15': 'BossInfo BossInfoServer',
 'R16': 'IItemPropertyGetter CooldownTracker ItemStackHelper InventoryHelper',
 'R23': 'ITooltipFlag IContainerListener',
 'R24': 'ITickable WorldSavedData SPacketUpdateTileEntity ILockableContainer TileEntityLockableLoot',
 'R25': 'RightClickBlock LivingDamageEvent',
 'R27': 'MultiPartEntityPart',
 'R28': 'EntityAreaEffectCloud',
}
TYPE_CAUSE = {name: group for group,names in TYPE_GROUPS.items() for name in names.split()}

def classify(e):
    msg=e['message']; sym=e.get('symbolName',''); name=sym.split('(')[0]; kind=e.get('symbolKind','')
    detail=e.get('details',''); source=detail.splitlines()[0] if detail else ''
    text=msg+' '+sym+' '+source
    for marker,group in [('constructor EntityAIHurtByTarget','R04'),('constructor found for PotionEffect','R06'),('Potion cannot be converted to int','R06'),('Vec3 cannot be converted to Vec3d','R17'),('from int to byte','R19'),('fall(float,float)','R20')]:
        if marker in msg:return group,'Diagnostic matches this explicit signature/type mismatch.'
    if 'getPosition' in msg and 'cannot be applied' in msg:return 'R21','Diagnostic names the incompatible position signature.'
    for marker,group in [('net.minecraft.advancements','R13'),('package BossInfo','R15'),('package Biome','R11'),('renderer.vertex','R08'),('structure.template','R11'),('package ForgeRegistries','R12'),('common.capabilities','R14'),('net.minecraftforge.items','R14')]:
        if marker in msg:return group,'Diagnostic names this missing package or nested-type family.'
    if 'Object cannot be converted to' in msg or 'method found for add(Object)' in msg:return 'R18','Diagnostic exposes Object/raw-collection typing; the exact declaration needs inspection.'
    if any(x in msg for x in ['illegal start','not a statement','inconvertible types','reference to']) or msg.startswith("';' expected"):
        return 'R31','Diagnostic reports syntax/cast/overload rejection; the underlying cause still needs source review.'
    if any(x in msg for x in ['does not override','does not take parameters','weaker access','name clash']):return 'R30','Diagnostic identifies an inheritance/generic/override contract mismatch.'
    if name in TYPE_CAUSE and kind in ('class','interface','variable','enum'):
        return TYPE_CAUSE[name],'Named symbol maps to this API family; individual receiver and behavior still require review.'
    if name in ('ARMOR','ARMOR_TOUGHNESS','ATTACK_SPEED') and 'SharedMonsterAttributes' in text+e.get('location',''):
        return 'R26','Attribute owner and symbol identify this combat API family.'
    groups={
      'R03':'setNoGravity hasNoGravity action Action',
      'R16':'isEmpty getCount setCount grow shrink splitStack EMPTY addPropertyOverride getCooldownTracker',
      'R22':'atan2 positiveModulo getInt clamp wrapDegrees',
      'R23':'drawHoveringText addChatComponentMessage ScaledResolution',
      'R01':'onInitialSpawn getDifficultyForLocation',
      'R02':'getHeldItem getHeldItemMainhand getHeldItemOffhand getActiveHand setActiveHand getItemStackFromSlot setItemStackToSlot',
      'R24':'markDirty getPos getWorld pos',
      'R08':'rotateCorpse setParticleTexture particleMaxAge particleScale particleRed particleGreen particleBlue renderParticle getTextureAtlasSprite spawnParticle',
      'R11':'getBiome getBiomeForCoordsBody getBiomeGenForCoords',
    }
    for group,names in groups.items():
        if name in names.split() or any(n+' in class ' in msg for n in names.split()):
            return group,'Named member suggests this API family; verify the receiver/superclass before choosing a fix.'
    if '/fluid/' in e['file'].replace('\\','/'):return 'R29','Inference from source package only; exact API cause is unclassified.'
    return 'R99','Unclassified exact cause; full compiler evidence is retained below.'

def sha(data):return hashlib.sha256(data).hexdigest()
def cell(value):return html.escape(str(value)).replace('|','&#124;').replace('\n','<br>')
def code(value):
    value=str(value);tick=chr(96)*(1+max((len(x) for x in re.findall(chr(96)+'+',value)),default=0))
    return tick+value+tick
def fence(value):
    token='~'*(1+max(2,max((len(x) for x in re.findall('~+',value)),default=0)))
    return token+'text\n'+value.rstrip()+'\n'+token+'\n'
def area(path):
    parts=path.replace('\\','/').split('/');return parts[6] if len(parts)>7 else 'root'

def main():
    inp=ROOT/'docs/compile-full-inventory.json';log=ROOT/'docs/compile-full-inventory.log'
    report=json.loads(inp.read_text(encoding='utf-8')); errors=report['errors'];sources=report['sourceFiles']
    assert report.get('sourceSnapshotStable') is True
    assert report['reportedErrors']==len(errors)
    current={p.relative_to(ROOT).as_posix() for p in (ROOT/'src/main/java').rglob('*.java')}
    assert current==set(sources),'Source-file set changed since compiler snapshot'
    lines={}
    for path,record in sources.items():
        data=(ROOT/path).read_bytes();assert sha(data)==record['sha256'],'Source changed: '+path
        lines[path]=data.decode('utf-8').splitlines()
    grouped=defaultdict(list);causes=Counter();areas=Counter();messages=Counter();symbols=Counter();bases=Counter()
    for number,e in enumerate(errors,1):
        path=e['file'].replace('\\','/');line=e['line'];details=e.get('details','')
        assert 1<=line<=len(lines[path]),(path,line)
        assert details.splitlines()[0].strip()==lines[path][line-1].strip(),(path,line,'javac/source mismatch')
        group,basis=classify(e);row=(number,e,group,basis);grouped[path].append(row)
        causes[group]+=1;areas[area(path)]+=1;messages[e['message']]+=1;bases[basis]+=1
        if e.get('symbol'):symbols[(e.get('symbolKind',''),e.get('symbolName',''))]+=1
    areas["backport"]+=0
    count=len(errors);files=len(grouped);out=[]
    status=json.loads((ROOT/'docs/port-status.json').read_text(encoding='utf-8'))
    base_count=sum(e['file'].endswith('EntityParasiteBase.java') for e in errors)
    def emit(s=''):out.append(s+'\n')
    def table(headers,rows):
        emit('| '+' | '.join(headers)+' |');emit('| '+' | '.join('---' for _ in headers)+' |')
        for row in rows:emit('| '+' | '.join(cell(v) for v in row)+' |')
        emit()
    emit('# All remaining captured compiler errors: Scape and Run: Parasites backport\n')
    emit('Report date: 2026-11-15. Input: recovered SRP 1.10.9 source originally targeting Minecraft 1.12.2. Target: Minecraft 1.7.10 / Forge 10.13.4.1614.\n')
    emit('**The complete mod still does not compile and is not playable.** This file includes every one of the **'+f'{count:,}'+f' captured compiler diagnostics across {files} files**, with the full available javac continuation, a matching converted-source location, and a cause explanation or an explicit uncertainty label.\n')
    emit('**Count limitation:** javac exited abnormally with code '+str(report['exitCode'])+'. The requested diagnostic limit was '+f"{report['maximumErrorsRequested']:,}"+' and was not reached. This is all captured output, not a guaranteed inventory of every defect. Diagnostics are not independent bugs, and their count is not a completion percentage. Some are cascades from a missing superclass/type.\n')
    emit('## Contents\n')
    emit('- [Major problems and why they remain](#major-problems)\n- [Counts by source area](#counts-by-source-area)\n- [Cause groups and explanations](#cause-groups)\n- [Every compiler message group](#every-compiler-message-group)\n- [Every named missing-symbol group](#every-named-missing-symbol-group)\n- [All source files](#all-source-files)\n- [Every diagnostic, grouped by file](#every-diagnostic)\n- [Provenance and regeneration](#provenance)\n')
    emit('<a id="major-problems"></a>\n\n## Major problems and why they remain\n')
    emit('1. **Shared entity behavior and AI:** remaining spawn integration, hands/equipment, movement/navigation and retaliation recruitment still use incompatible platform contracts. EntityParasiteBase alone has '+str(base_count)+' diagnostics; failures here cascade into many mobs. No complete original SRP mob has been validated end to end.\n2. **Client rendering and particles:** modern renderer, vertex, layer and particle contracts still require native implementations and client checks. The tested shared render-state adapter does not complete these renderers.\n3. **Specialized blocks and world generation:** shared block-state plumbing exists, but vanilla codecs, specialized inheritance/callbacks, biomes and structures remain incomplete.\n4. **Effects, inventories and persistence:** owning-player HUD visibility, potion items/types/brewing, missing effects, modern stack rules, capabilities, tile/world saved data and progression still need their original behavior preserved across save/reload and multiplayer. Stable effect IDs and server particle visibility have separate passing native tests.\n5. **Recovered source and API cascades:** missing types cause secondary symbol/override/generic errors. Recovered syntax and casts also need exact source/bytecode review; adding names alone will not establish correct behavior.\n6. **Build and gameplay validation:** the abnormal compiler exit is unresolved. A complete compile, reobfuscated JAR, original-content registration, fresh-world startup, restart, client and multiplayer checks are still required. Passing isolated compatibility tests is insufficient.\n')
    emit('Already implemented work includes '+str(status['compatibilityClasses'])+' compatibility source units, narrow access rules, entity query/position boundaries, typed entity synchronization, geometry/state adapters, local difficulty, the native spawn boundary, stable potion IDs, native accepted-application/finish hooks, persisted particle visibility, native armor/toughness and equipment-modifier/damage boundaries, real client tracker synchronization and part of native registration. Recorded verification: '+str(status['verificationGroupsPassed'])+' compatibility/native groups plus 3 diagnostic-parser/source-snapshot regression tests. All 1,094 original source units and 4,501 original assets are retained. See ARMOR_PORT.md for the latest implementation and test limits. Private-access diagnostics in this snapshot: zero.\n')
    emit('The converted sources may still retain a CFR header. That header is provenance, not evidence that the file is the untouched 1.12 reference. This report checks the actual converted files and hashes. Do not match its line numbers against preserved original-source files.\n')
    emit('Suggested sequence: stabilize the full compiler run; port remaining entity spawn/AI/equipment/effects; validate one original mob through registration, spawn, AI, damage, save/reload and synchronization; continue client rendering, specialized blocks and world/persistence integration; finally build and validate the complete mod.\n')
    emit('## Counts by source area\n');table(['Area','Diagnostics'],areas.most_common())
    emit('The backport area is the helper package. The original compatibility area contains cross-mod integrations and is a different source package.\n')
    emit('<a id="cause-groups"></a>\n\n## Cause groups and explanations\n')
    emit('Classification is conservative and automatic. A named API or signature identifies a useful family; it does not establish every expression\'s ultimate root cause. Per-error entries say which evidence assigned the group. R99 retains unclassified exact causes; R29 is explicitly a package-based inference.\n')
    emit('Unclassified exact causes (R99): **'+f"{causes['R99']:,}"+'**. Package-only fluid inferences (R29): **'+str(causes['R29'])+'**. All remain included in full below.\n')
    emit('| Cause | Family | Diagnostics |');emit('| --- | --- | --- |')
    for group,n in causes.most_common():emit('| ['+group+'](#'+group.lower()+') | '+cell(CAUSES[group][0])+' | '+str(n)+' |')
    emit()
    for group in sorted(causes):
        title,why,work=CAUSES[group];emit('<a id="'+group.lower()+'"></a>\n\n### '+group+': '+title+'\n')
        emit('Reported diagnostics: '+str(causes[group])+'.\n\nWhy: '+why+'\n\nRequired work: '+work+'\n')
    emit('## Every compiler message group\n');table(['Message','Occurrences'],messages.most_common())
    emit('## Every named missing-symbol group\n')
    emit('These counts use the parsed symbolKind/symbolName fields, including class, variable and method symbols. Different owners can share a name; the full per-error location below disambiguates them. Diagnostics without a symbol field still appear in the complete inventory.\n')
    table(['Kind','Symbol','Occurrences'],[(kind,name,n) for (kind,name),n in symbols.most_common()])
    emit('## All source files\n')
    anchors={path:'file-'+str(i).zfill(4) for i,path in enumerate(sorted(grouped),1)}
    emit('| Source file | Diagnostics |');emit('| --- | --- |')
    for path in sorted(grouped):emit('| ['+cell(path)+'](#'+anchors[path]+') | '+str(len(grouped[path]))+' |')
    emit('\n<a id="every-diagnostic"></a>\n\n## Every diagnostic\n')
    emit('IDs preserve the original compiler/probe order. Sections are sorted by file, so IDs within the document are not necessarily consecutive. No diagnostic has been deduplicated. Printed source lines were checked against the hashed converted source for every entry. Absolute links refer to this local checkout; the raw line and compiler evidence remain readable when the report is shared.\n')
    for path in sorted(grouped):
        emit('<a id="'+anchors[path]+'"></a>\n\n### '+code(path)+'\n')
        emit('Diagnostics: '+str(len(grouped[path]))+'. Source SHA256: '+code(sources[path]['sha256'])+'.\n')
        for number,e,group,basis in grouped[path]:
            title,why,work=CAUSES[group];line=e['line'];absolute=(ROOT/path).as_posix()
            emit('#### D'+str(number).zfill(5)+' — line '+str(line)+'\n')
            emit('Source: [line '+str(line)+'](<'+absolute+':'+str(line)+'>). Error: '+code(e['message'])+'.\n')
            if e.get('symbol'):emit('Symbol: '+code(e['symbol'])+'.'+(' Location: '+code(e['location'])+'.' if e.get('location') else '')+'\n')
            elif e.get('location'):emit('Location: '+code(e['location'])+'.\n')
            emit('Cause family: ['+group+' — '+title+'](#'+group.lower()+'). '+basis+'\n')
            emit('Why: '+why+'\n\nNext work: '+work+'\n')
            emit(fence(absolute+':'+str(line)+': error: '+e['message']+'\n'+e.get('details','')))
    emit('<a id="provenance"></a>\n\n## Provenance and regeneration\n')
    emit('The compiler source snapshot was stable before/after its run. This generator rechecked all '+str(len(sources))+' current source hashes, the source-file set and all '+str(count)+' diagnostic source lines. Report generation itself modifies no Java source, original asset, installed mod or save.\n')
    for p in (inp,log):emit('- Input '+code(p.name)+': SHA256 '+code(sha(p.read_bytes()))+'.')
    emit('\nSource snapshot schema: '+str(report['diagnosticSchemaVersion'])+'. Compiler exit: '+str(report['exitCode'])+'. No playable release is supplied by this report. The compiler exit code establishes abnormal termination; its exact internal cause is not established by the captured diagnostics.\n')
    emit('Regenerate after source changes by running the full probe first, then this generator. The probe returns a nonzero status while compilation fails.\n')
    emit(fence('python tools/compile_probe.py --limit 30000 --name compile-full-inventory\npython tools/write_remaining_errors.py\npython tools/rank_diagnostics.py --area entity --kind class --limit 30'))
    payload=''.join(out).encode('utf-8');text=payload.decode('utf-8')
    ids=re.findall(r'^#### D(\d{5}) — line ',text,re.M)
    assert len(ids)==count and set(map(int,ids))==set(range(1,count+1))
    assert sum(areas.values())==sum(causes.values())==sum(map(len,grouped.values()))==count
    target=ROOT/'docs/ALL_REMAINING_ERRORS.md';target.write_bytes(payload)
    manifest={'reportDate':'2026-11-15','reportFile':target.name,'reportSha256':sha(payload),'reportBytes':len(payload),'reportedDiagnostics':count,'filesWithDiagnostics':files,'compilerExitCode':report['exitCode'],'countIsExhaustive':False,'sourceHashesVerified':len(sources),'diagnosticSourceLinesVerified':count,'allDiagnosticIdsVerified':True,'fullModCompiles':False,'playable':False,'causeCounts':dict(causes.most_common()),'classificationBasisCounts':dict(bases),'unclassifiedExactCauses':causes['R99'],'packageOnlyInferences':causes['R29'],'inputHashes':{p.name:sha(p.read_bytes()) for p in (inp,log)}}
    mp=target.with_suffix('.manifest.json');mp.write_text(json.dumps(manifest,indent=2)+'\n',encoding='utf-8')
    zp=target.with_suffix('.zip')
    with zipfile.ZipFile(zp,'w',zipfile.ZIP_DEFLATED) as z:
        z.write(target,target.name);z.write(mp,mp.name)
    with zipfile.ZipFile(zp) as z:
        assert sha(z.read(target.name))==manifest['reportSha256']
        assert z.read(mp.name)==mp.read_bytes()
    print(json.dumps({'markdown':str(target),'bytes':len(payload),'diagnostics':count,'files':files,'unclassifiedExactCauses':causes['R99'],'sourceLinesVerified':count,'zip':str(zp)},indent=2))

if __name__=='__main__':main()
