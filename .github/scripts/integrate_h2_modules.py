from pathlib import Path

PATH = Path("androidApp/src/main/kotlin/io/github/mrsimkin/dndcustomaid/android/CharacterEditorV4.kt")
text = PATH.read_text()

if "CharacterTabV4.TECHNIQUES -> CharacterTechniquesModuleV4(" in text:
    raise SystemExit("H2 editor wiring already present; refusing duplicate integration")

anchor = '''                        CharacterTabV4.FORMS -> CharacterFormsModuleV4(
                            forms = h1ModuleDraft.forms,
                            closureState = closureState,
                            persistedFormIds = stored.forms.mapTo(mutableSetOf()) { it.id },
                            onFormsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(forms = updated))
                            },
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
'''

count = text.count(anchor)
if count != 1:
    raise SystemExit(f"Expected exactly one Forms wiring anchor, found {count}")

addition = '''                        CharacterTabV4.TECHNIQUES -> CharacterTechniquesModuleV4(
                            options = h1ModuleDraft.classOptions,
                            classes = settingsSheet.classes,
                            closureState = closureState,
                            persistedOptionIds = stored.classOptions.mapTo(mutableSetOf()) { it.id },
                            onOptionsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(classOptions = updated))
                            },
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.METAMAGIC -> CharacterMetamagicModuleV4(
                            options = h1ModuleDraft.classOptions,
                            classes = settingsSheet.classes,
                            closureState = closureState,
                            persistedOptionIds = stored.classOptions.mapTo(mutableSetOf()) { it.id },
                            onOptionsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(classOptions = updated))
                            },
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
                        CharacterTabV4.PACTS -> CharacterPactsModuleV4(
                            options = h1ModuleDraft.classOptions,
                            classes = settingsSheet.classes,
                            closureState = closureState,
                            persistedOptionIds = stored.classOptions.mapTo(mutableSetOf()) { it.id },
                            onOptionsChange = { updated ->
                                updateH1Modules(h1ModuleDraft.copy(classOptions = updated))
                            },
                            onClosureStateChange = ::persistClosureState,
                            wide = wide,
                            hapticsEnabled = closureState.hapticsEnabled,
                        )
'''

PATH.write_text(text.replace(anchor, anchor + addition, 1))
