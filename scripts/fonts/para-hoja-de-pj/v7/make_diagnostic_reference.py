from reportlab.pdfgen import canvas
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.pagesizes import A4, landscape
from reportlab.lib.units import mm
from reportlab.lib import colors
from pathlib import Path

FONT='/mnt/data/Para_Hoja_de_PJ_Symbols_v7.ttf'
OUT='/mnt/data/Para_Hoja_de_PJ_v7_diagnostic_reference.pdf'
pdfmetrics.registerFont(TTFont('PJv7', FONT))
W,H=landscape(A4); M=14*mm
c=canvas.Canvas(OUT,pagesize=(W,H), invariant=1); c.setTitle('Para Hoja de PJ Symbols v7 - Diagnostic and Reference'); c.setAuthor('Gustavo Munoz / DnD Custom Aid')
page=0

def header(title,subtitle=''):
    global page
    page+=1
    c.setFillColor(colors.black); c.setFont('Helvetica-Bold',19); c.drawString(M,H-M,title)
    if subtitle:
        c.setFont('Helvetica',9.5); c.drawString(M,H-M-16,subtitle)
    c.setFont('Helvetica',8); c.drawRightString(W-M,8*mm,f'v7 candidate - page {page}')

def glyph(ch,x,y,size):
    c.setFillColor(colors.black); c.setFont('PJv7',size)
    # Font asc/desc follows v1. Empirical visual centering uses baseline -0.39 size.
    c.drawCentredString(x,y-0.39*size,ch)

def label(x,y,text,bold=False,size=9,align='center'):
    c.setFillColor(colors.black); c.setFont('Helvetica-Bold' if bold else 'Helvetica',size)
    if align=='left': c.drawString(x,y,text)
    elif align=='right': c.drawRightString(x,y,text)
    else: c.drawCentredString(x,y,text)

def card(x,y,w,h,ch,title,sub='',size=78,code=''):
    c.setStrokeColor(colors.HexColor('#D0D0D0')); c.setLineWidth(.6); c.roundRect(x,y,w,h,3*mm,stroke=1,fill=0)
    glyph(ch,x+w/2,y+h*0.60,size)
    label(x+w/2,y+18*mm,title,True,10)
    if code: label(x+w/2,y+12*mm,code,False,8.3)
    if sub: label(x+w/2,y+6.5*mm,sub,False,8.1)

def end(): c.showPage()

# 1 contract
header('Para Hoja de PJ Symbols v7 - diagnostic + reference', "Candidate for owner visual review. v6 remains preserved; v7 refines the rejected 3/5/6/8/9 numerals and rebuilds marked containers from each shape's actual inner contour.")
lines=[
'1. A-E/a-e = exact historical v1 contours and historical meanings.',
'2. f-m + v/V + x/X + z = new glyphs drawn in the v1-derived heavy language.',
'3. n/o/p/P/O = historical A-E concepts redrawn in the v3-derived geometric language.',
'4. q-u + w/W + y/Y + Z = new glyphs in the v3-derived geometric language.',
'5. Extended icons exist in both design languages through style-specific PUA ranges.',
'6. Single/double checks retain the clean v6 conventional stroke geometry; double = two copies STACKED vertically.',
'7. Container marks are SHAPE-AWARE full interior spans: /, backslash, X, *, +, -, check and dot; endpoints follow each real inner contour without crossing the outline.',
'8. Digits 3, 5, 6, 8 and 9 were redrawn again in v7; 0, 1, 2, 4 and 7 retain the accepted v6 forms.',
'9. C/D retain the historical non-uniform oval. h/H is the additional uniform v1-style oval; p/P is the v3-style oval.',
'10. Renderer data stores semantics, not keyboard or PUA characters. PUA is rendering-layer API only.'
]
y=H-M-48
for i,t in enumerate(lines):
    c.setFont('Helvetica-Bold' if i in (0,5,6,7) else 'Helvetica',10.5); c.drawString(M,y-i*19,t)
label(M,H-M-244,'The next pages are intentionally large and sparse: this is an inspection document, not a compact catalog.',True,10,'left')
end()

# 2 historical compare
header('Historical v1 A-E vs v3-derived reinterpretation', 'Left column is exact 2007 geometry. Right column is the corresponding v3-derived geometric concept.')
rows=[('A/a','A','n','round outline','n'),('B/b','B','o','square outline','o'),('C/c','C','p','historical narrow oval outline','p'),('D/d','D','P','historical narrow oval filled','P'),('E/e','E','O','square filled','O')]
y0=H-M-52; rh=27*mm
label(84*mm,y0+8,'v1 exact',True,12); label(188*mm,y0+8,'v3-derived',True,12); label(247*mm,y0+8,'meaning',True,12)
for i,(key,a,b,meaning,bkey) in enumerate(rows):
    cy=y0-(i+1)*rh+9*mm
    c.setStrokeColor(colors.HexColor('#E0E0E0')); c.line(M,cy-13*mm,W-M,cy-13*mm)
    label(28*mm,cy,key,True,10)
    glyph(a,84*mm,cy,60); glyph(b,188*mm,cy,60)
    label(247*mm,cy,meaning,False,9)
end()

# 3 oval comparison
header('Oval family - historical, additional uniform, and v3-derived', 'The new uniform oval is retained, but it no longer replaces C/D.')
items=[('C','C / c','v1 historical outline','non-uniform historical contour'),('D','D / d','v1 historical filled','same historical silhouette'),('h','h','v1-style uniform outline','additional useful oval'),('H','H','v1-style uniform filled','additional useful oval'),('p','p','v3-style oval outline','geometric reinterpretation'),('P','P','v3-style oval filled','geometric reinterpretation')]
gap=6*mm; cw=(W-2*M-2*gap)/3; ch=67*mm
for idx,(chv,key,title,sub) in enumerate(items):
    r=idx//3; col=idx%3; x=M+col*(cw+gap); y=H-M-35*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,chv,title,sub,98,key)
end()

# 4 v1 core pairs
header('v1-derived new core glyphs', 'Human keyboard aliases. Lowercase = outline/empty; uppercase = filled/active for paired shapes.')
pairs=[('f/F','f','F','circle'),('g/G','g','G','square'),('h/H','h','H','uniform oval'),('i/I','i','I','diamond'),('j/J','j','J','triangle'),('k/K','k','K','hexagon'),('l/L','l','L','star'),('m/M','m','M','pip')]
y=H-M-48
for key,lo,up,meaning in pairs:
    label(M,y,key,True,10,'left'); glyph(lo,94*mm,y,48); glyph(up,137*mm,y,48); label(185*mm,y,meaning,False,10,'left'); y-=22.5*mm
end()

# 5 v3 core pairs
header('v3-derived geometric core glyphs', 'n/o/p/P/O carry the v3 reinterpretation of historical A-E concepts; q-u add new paired shapes.')
pairs=[('n/N','n','N','circle'),('o/O','o','O','square'),('p/P','p','P','oval (C/D concept)'),('q/Q','q','Q','diamond'),('r/R','r','R','triangle'),('s/S','s','S','hexagon'),('t/T','t','T','star'),('u/U','u','U','pip')]
y=H-M-48
for key,lo,up,meaning in pairs:
    label(M,y,key,True,10,'left'); glyph(lo,94*mm,y,48); glyph(up,137*mm,y,48); label(185*mm,y,meaning,False,10,'left'); y-=22.5*mm
end()

# 6 checks
header('Checks, expertise and status - both design languages', 'Each single mark is a conventional check. Double-check = two copies of that same check stacked vertically.')
items=[('v','v','v1 single check','Competent'),('V','V','v1 stacked double','Expertise / Pericia'),('x','x','v1 boxed check','checked state'),('X','X','v1 boxed stacked double','boxed expertise'),('z','z','v1 cross','failure/unavailable'),('w','w','v3 single check','Competent'),('W','W','v3 stacked double','Expertise / Pericia'),('y','y','v3 boxed check','checked state'),('Y','Y','v3 boxed stacked double','boxed expertise'),('Z','Z','v3 cross','failure/unavailable')]
gap=4*mm; cw=(W-2*M-4*gap)/5; ch=70*mm
for idx,(chv,key,title,sub) in enumerate(items):
    r=idx//5; col=idx%5; x=M+col*(cw+gap); y=H-M-34*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,chv,title,sub,78,key)
end()

# ordinary characters
header('Ordinary keyboard characters', 'Digits and common punctuation are real glyphs in the font. v7 specifically redraws 3/5/6/8/9 while retaining the accepted 0/1/2/4/7 forms.')

def char_cell(x,y,w,h,chv,name,size=38):
    c.setStrokeColor(colors.HexColor('#D0D0D0')); c.setLineWidth(.6); c.roundRect(x,y,w,h,3*mm,stroke=1,fill=0)
    glyph(chv,x+w/2,y+h*0.64,size)
    label(x+w/2,y+9*mm,name,True,8)
    label(x+w/2,y+4*mm,f'U+{ord(chv):04X}',False,7.2)

label(M,H-M-42,'NUMERALS',True,10,'left')
nums='0123456789'; gap=4*mm; cw=(W-2*M-9*gap)/10; y=H-M-125
for i,chv in enumerate(nums):
    x=M+i*(cw+gap); char_cell(x,y,cw,42*mm,chv,chv,40)

label(M,H-M-147,'COMMON PUNCTUATION',True,10,'left')
punct=[('-', 'hyphen/minus'),('_','underscore'),('+','plus'),('=','equals'),('/','slash'),('\\','backslash'),('|','bar'),('.','period'),(',','comma'),(':','colon'),(';','semicolon'),('*','asterisk'),('!','exclamation'),('?','question'),('#','hash'),('%','percent'),('(','paren left'),(')','paren right'),('[','bracket left'),(']','bracket right'),('{','brace left'),('}','brace right'),('<','less than'),('>','greater than'),('@','at'),('^','caret'),('~','tilde')]
cols=9; gap=4*mm; cw=(W-2*M-(cols-1)*gap)/cols; chh=31*mm
for idx,(chv,name) in enumerate(punct):
    r=idx//cols; col=idx%cols; x=M+col*(cw+gap); yy=H-M-164-(r+1)*chh-r*gap
    char_cell(x,yy,cw,chh,chv,name,25)
label(M,7*mm,'A-Z/a-z remain reserved for documented symbol aliases; ordinary digits and punctuation are available directly from the keyboard.',False,8.5,'left')
end()

# dedicated numeral QA
header('Ordinary numerals 0-9 - large visual inspection', 'v7 numeral QA: 3/5/6/8/9 redrawn for conventional shape, cleaner joins/counters, and better family consistency at large and sheet sizes.')
num_y=H/2+25*mm
for i,chv in enumerate('0123456789'):
    x=M+(i+.5)*(W-2*M)/10
    glyph(chv,x,num_y,82)
    label(x,num_y-43*mm,chv,True,10)
label(M,26*mm,'Also verify at sheet scale:',True,9,'left')
for size,yy in [(32,20*mm),(22,12*mm)]:
    label(55*mm,yy+2*mm,f'{size} pt',False,8,'right')
    c.setFont('PJv7',size); c.drawString(62*mm,yy,'0123456789  -12  +3  20/20  100%')
end()

# dedicated full-span mark interpretation QA
header('Full-span internal mark semantics - large inspection', "v7 derives each stroke endpoint from the container's actual fillable inner contour: full-looking spans with no outline overlap, white wedges, or arbitrary short lines.")
marks_large=[('slash','/'),('backslash','backslash'),('x','X'),('asterisk','*'),('plus','+'),('minus','-'),('check','check'),('dot','dot')]
examples=[('v1 circle',0xE400),('v1 historical oval',0xE410),('v1 heart',0xE440),('v3 square',0xE508),('v3 circle',0xE500),('v3 heart',0xE538)]
left=55*mm; top=H-M-48; colw=(W-left-M)/8; rowh=25*mm
for j,(mn,ml) in enumerate(marks_large): label(left+(j+.5)*colw,top+8,ml,True,8.5)
for r,(name,basecp) in enumerate(examples):
    yy=top-(r+1)*rowh+7*mm
    label(M,yy,name,True,8.5,'left')
    for j,_ in enumerate(marks_large): glyph(chr(basecp+j),left+(j+.5)*colw,yy,42)
end()

# marked fillable/container variants
marks=[('slash','/'),('backslash','\\'),('x','X'),('asterisk','*'),('plus','+'),('minus','-'),('check','check'),('dot','dot')]
v1_shapes=['circle','square','oval historical','oval uniform','diamond','triangle','hexagon','star','heart','shield','drop']
v3_shapes=['circle','square','oval','diamond','triangle','hexagon','star','heart','shield','drop']

def marked_pages(title_prefix, base, shapes, generic=False):
    per_page=4
    for part in range((len(shapes)+per_page-1)//per_page):
        subset=shapes[part*per_page:(part+1)*per_page]
        namespace='generic v3-derived E600+' if generic else f'U+{base:04X} style-specific range'
        header(f'{title_prefix} ({part+1}/{(len(shapes)+per_page-1)//per_page})', f'Precomposed FULL-SPAN interior states. Columns: /, backslash, X, *, +, -, check, dot. Namespace: {namespace}.')
        left=47*mm; top=H-M-48; colw=(W-left-M)/8; rowh=35*mm
        for j,(mn,ml) in enumerate(marks): label(left+(j+.5)*colw,top+8,ml,True,9)
        for r,shape in enumerate(subset):
            shape_idx=part*per_page+r; yy=top-(r+1)*rowh+11*mm
            label(M,yy,shape,True,8.5,'left')
            for j,(mn,ml) in enumerate(marks):
                cp=base+shape_idx*8+j; glyph(chr(cp),left+(j+.5)*colw,yy,36)
                label(left+(j+.5)*colw,yy-12*mm,f'{cp:04X}',False,6.8)
        end()

marked_pages('Marked containers - v1-derived',0xE400,v1_shapes)
marked_pages('Marked containers - v3-derived',0xE500,v3_shapes)
marked_pages('Marked containers - generic renderer aliases',0xE600,v3_shapes,True)

# v1 extended icons
header('Extended useful symbols - v1-derived language', 'PUA-first icons intended for this and future character sheets: HP, defense, spell/effect, energy, state, duration, notes and targeting.')
base=0xE200
# indices 22..39
icons=[(22,'heart','HP/life'),(24,'shield','AC/defense'),(26,'flame','fire/effect'),(28,'bolt','initiative/energy'),(30,'drop','resource/blood/water'),(32,'eye','perception/concentration'),(34,'hourglass','duration/turn'),(36,'book','spellbook/notes'),(38,'target','attack/aim'),(39,'bullseye','marked/critical')]
gap=4*mm; cw=(W-2*M-4*gap)/5; ch=70*mm
for idx,(off,nm,use) in enumerate(icons):
    cp=base+off; r=idx//5; col=idx%5; x=M+col*(cw+gap); y=H-M-34*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,chr(cp),nm,use,75,f'U+{cp:04X}')
end()

# 8 v3 extended icons
header('Extended useful symbols - v3-derived geometric language', 'Same semantics as the v1-derived page; this lets each sheet family choose one coherent visual language.')
base=0xE300
for idx,(off,nm,use) in enumerate(icons):
    cp=base+off; r=idx//5; col=idx%5; x=M+col*(cw+gap); y=H-M-34*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,chr(cp),nm,use,75,f'U+{cp:04X}')
end()

# 9 standard aliases
header('Standard-use aliases', 'Common Unicode symbols map to the v3-derived forms; a few ASCII composition characters are also present.')
std=[('○','U+25CB','circle outline'),('●','U+25CF','circle filled'),('□','U+25A1','square outline'),('■','U+25A0','square filled'),('◇','U+25C7','diamond outline'),('◆','U+25C6','diamond filled'),('△','U+25B3','triangle outline'),('▲','U+25B2','triangle filled'),('☆','U+2606','star outline'),('★','U+2605','star filled'),('✓','U+2713','check'),('✕','U+2715','cross'),('♡','U+2661','heart outline'),('♥','U+2665','heart filled'),('◎','U+25CE','double circle'),('⊙','U+2299','bullseye'),('⚡','U+26A1','bolt filled')]
cols=6; gap=4*mm; cw=(W-2*M-(cols-1)*gap)/cols; ch=44*mm
for idx,(char,code,meaning) in enumerate(std):
    r=idx//cols; col=idx%cols; x=M+col*(cw+gap); y=H-M-31*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,char,meaning,'',44,code)
label(M,12*mm,'Ordinary ASCII block also includes 0-9, - _ + = / backslash | . , : ; * ! ? # % ( ) [ ] { } < > @ ^ ~',True,8.5,'left')
end()

# 10 keyboard cheat sheet
header('Keyboard reference - human entry aliases', 'Renderer code should prefer semantic PUA aliases; these aliases are for convenient manual use and specimen entry.')
entries=[
('A/a','v1 exact round outline'),('B/b','v1 exact square outline'),('C/c','v1 exact historical oval outline'),('D/d','v1 exact historical oval filled'),('E/e','v1 exact square filled'),
('f/F','v1 circle outline/filled'),('g/G','v1 square outline/filled'),('h/H','v1 uniform oval outline/filled'),('i/I','v1 diamond outline/filled'),('j/J','v1 triangle outline/filled'),('k/K','v1 hexagon outline/filled'),('l/L','v1 star outline/filled'),('m/M','v1 pip outline/filled'),
('v/V','v1 check / stacked double-check'),('x/X','v1 boxed check / boxed stacked double'),('z','v1 cross'),
('n/N','v3 circle outline/filled'),('o/O','v3 square outline/filled'),('p/P','v3 oval outline/filled'),('q/Q','v3 diamond outline/filled'),('r/R','v3 triangle outline/filled'),('s/S','v3 hexagon outline/filled'),('t/T','v3 star outline/filled'),('u/U','v3 pip outline/filled'),('w/W','v3 check / stacked double-check'),('y/Y','v3 boxed check / boxed stacked double'),('Z','v3 cross')]
colw=(W-2*M)/2; y=H-M-40
for i,(key,meaning) in enumerate(entries):
    col=0 if i<14 else 1; row=i if col==0 else i-14; xx=M+col*colw; yy=y-row*13.2
    label(xx,yy,key,True,8.7,'left'); label(xx+27*mm,yy,meaning,False,8.7,'left')
end()

# 11-12 generic PUA
pua_generic=[
(0xE000,'circle outline','resource/slot/counter'),(0xE001,'circle filled','active/spent'),(0xE002,'double circle','legacy expertise/target'),(0xE003,'square outline','checkbox base'),(0xE004,'square filled','active square'),(0xE005,'check','generic check'),(0xE006,'cross','failure/unavailable'),(0xE007,'boxed check','checked box'),(0xE008,'diamond outline','state/attunement'),(0xE009,'diamond filled','active state'),(0xE00A,'oval outline','oval state'),(0xE00B,'oval filled','active oval'),(0xE00C,'stacked double-check','Expertise / Pericia'),(0xE00D,'boxed stacked double','boxed Expertise'),(0xE00E,'triangle outline','warning/state'),(0xE00F,'triangle filled','active warning'),(0xE010,'hex outline','resource/state'),(0xE011,'hex filled','active hex'),(0xE012,'star outline','special/inspiration'),(0xE013,'star filled','active inspiration'),(0xE014,'pip outline','small counter'),(0xE015,'pip filled','small active counter'),(0xE016,'heart outline','HP/life'),(0xE017,'heart filled','active HP/life'),(0xE018,'shield outline','AC/defense'),(0xE019,'shield filled','active defense'),(0xE01A,'flame outline','fire/effect'),(0xE01B,'flame filled','active effect'),(0xE01C,'bolt outline','initiative/energy'),(0xE01D,'bolt filled','active energy'),(0xE01E,'drop outline','resource/blood/water'),(0xE01F,'drop filled','active drop'),(0xE020,'eye outline','perception/concentration'),(0xE021,'eye filled','active eye'),(0xE022,'hourglass outline','duration/turn'),(0xE023,'hourglass filled','active duration'),(0xE024,'book outline','spellbook/notes'),(0xE025,'book filled','active book'),(0xE026,'target','attack/aim'),(0xE027,'bullseye','marked/critical')]
for part in range(2):
    header(f'Generic renderer PUA reference ({part+1}/2)', 'Primary renderer namespace uses v3-derived geometric forms. Style-specific ranges E200/E300 are available when a sheet explicitly needs one family.')
    subset=pua_generic[part*20:(part+1)*20]
    cols=5; gap=4*mm; cw=(W-2*M-4*gap)/5; ch=36*mm
    for idx,(cp,nm,use) in enumerate(subset):
        r=idx//5; col=idx%5; x=M+col*(cw+gap); y=H-M-30*mm-(r+1)*ch-r*gap
        card(x,y,cw,ch,chr(cp),nm,use,42,f'U+{cp:04X}')
    end()

# 13 style ranges summary
header('Style-specific PUA ranges', 'Core: E200+ = v1-derived; E300+ = v3-derived. Marked containers: E400+ v1, E500+ v3, E600+ generic v3 aliases.')
concepts=['circle outline','circle filled','double circle','square outline','square filled','oval outline','oval filled','diamond outline','diamond filled','triangle outline','triangle filled','hex outline','hex filled','star outline','star filled','pip outline','pip filled','check','stacked double-check','boxed check','boxed stacked double','cross','heart outline','heart filled','shield outline','shield filled','flame outline','flame filled','bolt outline','bolt filled','drop outline','drop filled','eye outline','eye filled','hourglass outline','hourglass filled','book outline','book filled','target','bullseye']
label(M,H-M-42,'Concept',True,9,'left'); label(112*mm,H-M-42,'v1-derived',True,9); label(178*mm,H-M-42,'v3-derived',True,9)
y=H-M-57
for i,nm in enumerate(concepts):
    col=0 if i<20 else 1; row=i if col==0 else i-20; xx=M+col*132*mm; yy=y-row*8.1*mm
    label(xx,yy,nm,False,7.7,'left'); label(xx+73*mm,yy,f'U+{0xE200+i:04X}',False,7.7); label(xx+106*mm,yy,f'U+{0xE300+i:04X}',False,7.7)
end()

# 14 semantic aliases
header('Semantic aliases planned for character-sheet rendering', 'Compatibility E100-E109 is preserved; E10A-E118 add sheet-oriented semantics. Domain state still remains semantic, not encoded as glyph characters.')
sem=[(0xE100,'legacy proficient marker','compat: filled circle'),(0xE101,'legacy expertise marker','compat: double circle'),(0xE102,'CHECKBOX_EMPTY','square outline'),(0xE103,'CHECKBOX_CHECKED_MARK','single check'),(0xE104,'SLOT_AVAILABLE','circle outline'),(0xE105,'SLOT_SPENT','circle filled'),(0xE106,'COUNTER_EMPTY','circle outline'),(0xE107,'COUNTER_FILLED','circle filled'),(0xE108,'PROFICIENT_CHECK','Competent = one check'),(0xE109,'EXPERTISE_DOUBLE_CHECK','Expertise = stacked double-check'),(0xE10A,'BOXED_CHECK','checked box'),(0xE10B,'BOXED_EXPERTISE','boxed stacked double'),(0xE10C,'DEATH_SAVE_SUCCESS','check'),(0xE10D,'DEATH_SAVE_FAILURE','cross'),(0xE10E,'INSPIRATION_ACTIVE','filled star'),(0xE10F,'ATTUNED_ACTIVE','filled diamond'),(0xE110,'CONCENTRATION','eye outline'),(0xE111,'DEFENSE_AC','shield outline'),(0xE112,'HP_LIFE','filled heart'),(0xE113,'FIRE_EFFECT','filled flame'),(0xE114,'INITIATIVE_ENERGY','filled bolt'),(0xE115,'DURATION_TURN','hourglass outline'),(0xE116,'SPELLBOOK_NOTES','book outline'),(0xE117,'ATTACK_TARGET','target'),(0xE118,'MARKED_CRITICAL','bullseye')]
cols=5; gap=4*mm; cw=(W-2*M-4*gap)/5; ch=39*mm
for idx,(cp,nm,use) in enumerate(sem):
    r=idx//5; col=idx%5; x=M+col*(cw+gap); y=H-M-30*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,chr(cp),nm,use,40,f'U+{cp:04X}')
end()

# 15 practical usage examples
header('Reference: proposed use in PC sheets', 'These are rendering references, not final layout approval. They define a stable visual vocabulary for current and future sheets.')
examples=[
('Skill proficient',chr(0xE108),'one check','Skills / saving throws'),('Skill expertise',chr(0xE109),'stacked double-check','Skills / custom skills'),('Spell slot available',chr(0xE104),'empty circle','Spell lists / resources'),('Spell slot spent',chr(0xE105),'filled circle','Spell lists / resources'),('Death save success',chr(0xE10C),'check','Main / state'),('Death save failure',chr(0xE10D),'cross','Main / state'),('Inspiration',chr(0xE10E),'filled star','Main / special state'),('Attuned',chr(0xE10F),'filled diamond','Equipment / magic items'),('Concentration',chr(0xE110),'eye','Spells / current effects'),('Armor / defense',chr(0xE111),'shield','Main / combat'),('HP / life',chr(0xE112),'heart','Main / companion / forms'),('Initiative / energy',chr(0xE114),'bolt','Combat / resources'),('Duration',chr(0xE115),'hourglass','Effects / conditions'),('Spellbook / notes',chr(0xE116),'book','Spellbook / notes'),('Attack target',chr(0xE117),'target','Combat'),('Marked / critical',chr(0xE118),'bullseye','Combat / status')]
cols=4; gap=5*mm; cw=(W-2*M-3*gap)/4; ch=48*mm
for idx,(name,chv,meaning,where) in enumerate(examples):
    r=idx//4; col=idx%4; x=M+col*(cw+gap); y=H-M-31*mm-(r+1)*ch-r*gap
    card(x,y,cw,ch,chv,name,where,48,meaning)
end()

# 16 large 72 pt
header('Large diagnostic stress - 72 pt', 'Failure-prone contours at very large size. One glyph per row prevents layout overlap from masquerading as a font defect.')
rows=[('historical oval','C'),('v1 uniform oval','h'),('v3 oval','p'),('v1 stacked double','V'),('v3 stacked double','W'),('v3 boxed stacked double','Y')]
y=H-M-65
for nm,chv in rows:
    label(M,y,nm,False,9,'left'); glyph(chv,220*mm,y,72); y-=25*mm
end()

# 17 large 48 pt
header('Large diagnostic stress - 48 pt', 'All critical check/status forms at a size large enough to inspect joins, counters and internal clearance.')
rows=[('historical oval','C'),('v1 uniform oval','h'),('v3 oval','p'),('v1 check','v'),('v1 stacked double','V'),('v1 boxed stacked double','X'),('v1 cross','z'),('v3 check','w'),('v3 stacked double','W'),('v3 boxed stacked double','Y'),('v3 cross','Z')]
y=H-M-61
for nm,chv in rows:
    label(M,y,nm,False,8.7,'left'); glyph(chv,220*mm,y,48); y-=14.2*mm
end()

# 18 compact stress
header('Compact-use stress - sheet-scale symbols', '32 / 24 / 16 pt across core and extended symbols. This is the practical legibility gate for compact PDF fields.')
rows=[('historical oval','C'),('v1 uniform oval','h'),('v3 oval','p'),('v1 check','v'),('v1 stacked double','V'),('v1 boxed stacked double','X'),('v1 cross','z'),('v3 check','w'),('v3 stacked double','W'),('v3 boxed stacked double','Y'),('v3 cross','Z'),('heart',chr(0xE016)),('shield',chr(0xE018)),('eye',chr(0xE020)),('hourglass',chr(0xE022)),('book',chr(0xE024)),('target',chr(0xE026)),('bullseye',chr(0xE027))]
sizes=[32,24,16]; xs=[145,205,260]
for xx,sz in zip(xs,sizes): label(xx*mm,H-M-41,f'{sz} pt',True,9)
y=H-M-58
for nm,chv in rows:
    label(M,y,nm,False,8.2,'left')
    for xx,sz in zip(xs,sizes): glyph(chv,xx*mm,y,sz)
    y-=8.2*mm
end()

c.save(); print(OUT)
