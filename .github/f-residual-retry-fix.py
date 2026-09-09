from pathlib import Path

p = Path('.github/f-owner-retest-residual-once.py')
text = p.read_text(encoding='utf-8')
start_marker = "replace_once(\n    spell,\n    '''        modifier = Modifier.fillMaxWidth()"
end_marker = "\n\nsource_editor ="
start = text.find(start_marker)
if start < 0:
    raise SystemExit('could not locate old spell multiline helper block')
end = text.find(end_marker, start)
if end < 0:
    raise SystemExit('could not locate end of spell multiline helper block')
replacement = '''replace_once(\n    spell,\n    '            label = { Text("Componente material (opcional)") },\\n            modifier = Modifier.fillMaxWidth(),',\n    '            label = { Text("Componente material (opcional)") },\\n            modifier = Modifier.fillMaxWidth(),\\n            minLines = 2,\\n            maxLines = 4,',\n)\nreplace_once(\n    spell,\n    '        label = { Text("Descripción") },\\n        modifier = Modifier.fillMaxWidth(),\\n        minLines = 4,\\n        maxLines = 10,',\n    '        label = { Text("Descripción") },\\n        modifier = Modifier.fillMaxWidth(),\\n        minLines = 3,\\n        maxLines = 5,',\n)\nreplace_once(\n    spell,\n    '        label = { Text("Notas (opcional)") },\\n        modifier = Modifier.fillMaxWidth(),\\n        minLines = 2,\\n        maxLines = 6,',\n    '        label = { Text("Notas (opcional)") },\\n        modifier = Modifier.fillMaxWidth(),\\n        minLines = 2,\\n        maxLines = 4,',\n)'''
p.write_text(text[:start] + replacement + text[end:], encoding='utf-8')
print('retry helper corrected')
