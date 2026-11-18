"""Rank complete javac missing-symbol diagnostics by source area and symbol kind."""
from pathlib import Path
import argparse,collections,json
R=Path(__file__).resolve().parents[1]
def main():
 p=argparse.ArgumentParser();p.add_argument('--report',default='compile-full-inventory');p.add_argument('--area');p.add_argument('--kind',choices=['class','interface','enum','method','variable']);p.add_argument('--limit',type=int,default=30);a=p.parse_args()
 assert a.report.replace('-','').isalnum() and a.limit>0
 report=json.loads((R/'docs'/(a.report+'.json')).read_text(encoding='utf-8'));rows=report['errors']
 if a.area:rows=[r for r in rows if r['file'].replace('\\','/').split('/')[6]==a.area]
 counts=collections.Counter(r['symbol'] for r in rows if 'symbol' in r and (a.kind is None or r.get('symbolKind')==a.kind))
 print('Reported diagnostics:',len(rows),'Named symbols:',sum('symbol' in r for r in rows),'Compiler exit:',report['exitCode'])
 for symbol,count in counts.most_common(a.limit):print(str(count).rjust(5),symbol)
if __name__=='__main__':main()
