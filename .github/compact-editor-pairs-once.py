from pathlib import Path

ROOT = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android")


def replace_once(path: Path, old: str, new: str, label: str) -> None:
    text = path.read_text(encoding="utf-8")
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected 1 occurrence, found {count}")
    path.write_text(text.replace(old, new, 1), encoding="utf-8")


# Background: short identity fields stay in one row even on phone; touched spacing follows app density.
p = ROOT / "CharacterBackgroundTabV4.kt"
text = p.read_text(encoding="utf-8")
old = '''                    if (wide) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(7.dp)),
                        ) {
                            OutlinedTextField(
                                value = background.race,
                                onValueChange = { onBackgroundChange(background.copy(race = it)) },
                                enabled = structuralEditingEnabled,
                                modifier = Modifier.weight(1f),
                                label = { Text("Raza") },
                                singleLine = true,
                            )
                            OutlinedTextField(
                                value = background.religionFaith,
                                onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },
                                enabled = structuralEditingEnabled,
                                modifier = Modifier.weight(1f),
                                label = { Text("Religión / Fe") },
                                singleLine = true,
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = background.race,
                            onValueChange = { onBackgroundChange(background.copy(race = it)) },
                            enabled = structuralEditingEnabled,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Raza") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = background.religionFaith,
                            onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },
                            enabled = structuralEditingEnabled,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Religión / Fe") },
                            singleLine = true,
                        )
                    }
'''
new = '''                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
                    ) {
                        OutlinedTextField(
                            value = background.race,
                            onValueChange = { onBackgroundChange(background.copy(race = it)) },
                            enabled = structuralEditingEnabled,
                            modifier = Modifier.weight(1f),
                            label = { Text("Raza") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            value = background.religionFaith,
                            onValueChange = { onBackgroundChange(background.copy(religionFaith = it)) },
                            enabled = structuralEditingEnabled,
                            modifier = Modifier.weight(1f),
                            label = { Text("Religión / Fe") },
                            singleLine = true,
                        )
                    }
'''
if text.count(old) != 1:
    raise RuntimeError(f"background race/religion pair: expected 1 occurrence, found {text.count(old)}")
text = text.replace(old, new, 1)
text = text.replace('start = if (wide) 10.dp else 5.dp,', 'start = appSpacingV4(if (wide) 10.dp else 5.dp),', 1)
text = text.replace('end = if (wide) 10.dp else 5.dp,', 'end = appSpacingV4(if (wide) 10.dp else 5.dp),', 1)
text = text.replace('top = 5.dp,', 'top = appSpacingV4(5.dp),', 1)
for old_pad, new_pad in [
    ('padding(horizontal = 8.dp, vertical = 7.dp)', 'padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(5.dp))'),
    ('padding(horizontal = 7.dp, vertical = 6.dp)', 'padding(horizontal = appSpacingV4(7.dp), vertical = appSpacingV4(5.dp))'),
]:
    text = text.replace(old_pad, new_pad)
p.write_text(text, encoding="utf-8")

# Combat editor: attack modifier + range are both compact attack-reference fields.
p = ROOT / "CharacterCombatTabV4.kt"
replace_once(
    p,
    '''        OutlinedTextField(
            value = attackModifier,
            onValueChange = onAttackModifierChange,
            label = { Text("Modificador de ataque (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
        OutlinedTextField(
            value = damageEffect,
            onValueChange = onDamageEffectChange,
            label = { Text("Daño / efecto") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )
        OutlinedTextField(
            value = range,
            onValueChange = onRangeChange,
            label = { Text("Alcance (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
''',
    '''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            OutlinedTextField(
                value = attackModifier,
                onValueChange = onAttackModifierChange,
                label = { Text("Ataque (opcional)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
            OutlinedTextField(
                value = range,
                onValueChange = onRangeChange,
                label = { Text("Alcance (opcional)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }
        OutlinedTextField(
            value = damageEffect,
            onValueChange = onDamageEffectChange,
            label = { Text("Daño / efecto") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4,
        )
''',
    "combat attack/range pair",
)

# General closure: compact related sense and movement editor pairs.
p = ROOT / "CharacterGeneralClosureV4.kt"
replace_once(
    p,
    '''        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Sentido") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = range, onValueChange = { range = it.filter(Char::isDigit) }, label = { Text("Alcance en pies (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
''',
    '''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Sentido") },
                modifier = Modifier.weight(1.25f),
                singleLine = true,
            )
            OutlinedTextField(
                value = range,
                onValueChange = { range = it.filter(Char::isDigit) },
                label = { Text("Alcance (pies)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }
''',
    "sense name/range pair",
)
replace_once(
    p,
    '''        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(value = speed, onValueChange = { speed = it.filter(Char::isDigit) }, label = { Text("Velocidad en pies (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
''',
    '''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.weight(1.25f),
                singleLine = true,
            )
            OutlinedTextField(
                value = speed,
                onValueChange = { speed = it.filter(Char::isDigit) },
                label = { Text("Velocidad (pies)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
        }
''',
    "movement name/speed pair",
)

# PC Settings: custom characteristic abbreviation + score are one compact metadata row.
p = ROOT / "CharacterPcSuccessorSettingsV4.kt"
replace_once(
    p,
    '''        OutlinedTextField(abbreviation, { abbreviation = it.take(8) }, label = { Text("Abreviatura") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        OutlinedTextField(
            scoreText,
            { raw -> scoreText = raw.filter(Char::isDigit) },
            label = { Text("Puntuación") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )
''',
    '''        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(appSpacingV4(6.dp)),
        ) {
            OutlinedTextField(
                abbreviation,
                { abbreviation = it.take(8) },
                label = { Text("Abreviatura") },
                modifier = Modifier.weight(1f),
                singleLine = true,
            )
            OutlinedTextField(
                scoreText,
                { raw -> scoreText = raw.filter(Char::isDigit) },
                label = { Text("Puntuación") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
''',
    "custom attribute abbreviation/score pair",
)
text = p.read_text(encoding="utf-8")
old_card = 'modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 7.dp),'
if text.count(old_card) != 1:
    raise RuntimeError(f"successor setting card padding: expected 1 occurrence, found {text.count(old_card)}")
text = text.replace(
    old_card,
    'modifier = Modifier.fillMaxWidth().padding(horizontal = appSpacingV4(8.dp), vertical = appSpacingV4(5.dp)),',
    1,
)
p.write_text(text, encoding="utf-8")

# Outcome guards.
background = (ROOT / "CharacterBackgroundTabV4.kt").read_text(encoding="utf-8")
if 'if (wide) {\n                        Row(' in background and 'background.race' in background.split('if (wide) {\n                        Row(', 1)[1][:1800]:
    raise RuntimeError("Background race/religion still conditional on wide layout")
combat = (ROOT / "CharacterCombatTabV4.kt").read_text(encoding="utf-8")
if 'Modificador de ataque (opcional)' in combat:
    raise RuntimeError("Old long combat attack label remains")
settings = (ROOT / "CharacterPcSuccessorSettingsV4.kt").read_text(encoding="utf-8")
if 'padding(horizontal = 8.dp, vertical = 7.dp)' in settings:
    raise RuntimeError("Raw successor setting card padding remains")

print("Applied guarded compact editor-pair and density-authority pass")
