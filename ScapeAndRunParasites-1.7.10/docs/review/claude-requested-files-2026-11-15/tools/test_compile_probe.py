import unittest,tempfile,hashlib
from pathlib import Path
from compile_probe import parse_diagnostics,snapshot_sources,ROOT
class DiagnosticParsingTests(unittest.TestCase):
 def test_adjacent_symbols_keep_their_own_locations(self):
  path=str(ROOT/'src/main/java/Example.java')
  text=path+""":4: error: cannot find symbol
        EnumHand value;
        ^
  symbol:   class EnumHand
  location: class Example
"""+path+""":8: error: cannot find symbol
        value.missing();
             ^
  symbol:   method missing()
  location: variable value of type Example
"""
  rows=parse_diagnostics(text);self.assertEqual(len(rows),2);self.assertEqual(rows[0]['symbolName'],'EnumHand');self.assertEqual(rows[0]['symbolKind'],'class');self.assertEqual(rows[0]['location'],'class Example');self.assertEqual(rows[1]['symbol'],'method missing()');self.assertNotIn('EnumHand',rows[1]['details'])
 def test_overload_details_survive_without_a_symbol(self):
  path=str(ROOT/'src/main/java/Example.java')
  rows=parse_diagnostics(path+""":12: error: method getPosition cannot be applied to given types;
 getPosition();
 ^
  required: float
  found: no arguments
  reason: actual and formal argument lists differ in length
""")
  self.assertEqual(rows[0]['required'],'float');self.assertEqual(rows[0]['found'],'no arguments');self.assertIn('reason',rows[0]);self.assertNotIn('symbol',rows[0]);self.assertIn('getPosition();',rows[0]['details'])
 def test_source_snapshot_tracks_bytes_and_line_numbers(self):
  with tempfile.TemporaryDirectory() as folder:
   root=Path(folder);path=root/'Example.java';original=b'class Example {\r\n}\r\n';path.write_bytes(original)
   before=snapshot_sources([path],root);self.assertEqual(before['Example.java']['lineCount'],2);self.assertEqual(before['Example.java']['sha256'],hashlib.sha256(original).hexdigest())
   path.write_bytes(b'// added line\n'+original);after=snapshot_sources([path],root);self.assertEqual(after['Example.java']['lineCount'],3);self.assertNotEqual(before,after)
if __name__=='__main__':unittest.main()
