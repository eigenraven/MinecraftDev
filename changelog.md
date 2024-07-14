# Minecraft Development for IntelliJ

## [1.8.0]

This release contains two major features:
- Support for MixinExtras expressions ([#2274](https://github.com/minecraft-dev/MinecraftDev/pull/2274))
- A rewritten project creator ([#2304](https://github.com/minecraft-dev/MinecraftDev/pull/2304))

### About the new project creator

The new project creator is very similar to the previous one but has a few advantages:
- The templates are now stored on a separate repository and updated the first time you open the creator. This allows us to release template updates independently of plugin releases.
- You can create your own custom templates in their own repositories, which can be:
  - flat directories
  - local ZIP archives
  - remote ZIP archives (like the built-in templates)
- Kotlin templates were added to all platforms except Forge and Architectury (couldn't get the Forge one to work, will look into it later)
- Fabric now has a split sources option
- Some niche options like the plugins dependencies fields were removed as their use was quite limited
- Remembered field values won't be ported over to the new creator, so make sure to configure your Group ID under Build System Properties!
- The old creator will be kept for a few months to give us the time to fix the new creator, please report any issues on the [issue tracker](https://github.com/minecraft-dev/MinecraftDev/issues)

### Added

- Initial support for NeoForge's ModDevGradle
- Option to force json translation and configurable default i18n call ([#2292](https://github.com/minecraft-dev/MinecraftDev/pull/2292))
- Minecraft version detection for Loom-based projects
- Other JVM languages support for translation references, inspections and code folding
- Repo-based project creator templates ([#2304](https://github.com/minecraft-dev/MinecraftDev/pull/2304))
- Support for MixinExtras expressions ([#2274](https://github.com/minecraft-dev/MinecraftDev/pull/2274))

### Changed

- [#2296](https://github.com/minecraft-dev/MinecraftDev/issues/2296) Support entry point container objects in fabric.mod.json
- [#2325](https://github.com/minecraft-dev/MinecraftDev/issues/2325) Make lang annotator fixes bulk compatible
- Migrated the remaining legacy forms to the Kotlin UI DSL

### Fixed

- [#2316](https://github.com/minecraft-dev/MinecraftDev/issues/2316) Sponge's injection inspection isn't aware of the Configurate 4 classes ([#2317](https://github.com/minecraft-dev/MinecraftDev/pull/2317))
- [#2310](https://github.com/minecraft-dev/MinecraftDev/issues/2310) Translations aren't detected for enum constructors
- [#2260](https://github.com/minecraft-dev/MinecraftDev/issues/2260) Vararg return type expected in a @ModifyArg method
