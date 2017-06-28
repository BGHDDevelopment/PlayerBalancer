# LobbyBalancer
[Spigot Resource](https://www.spigotmc.org/resources/10788/)

[![Build Status](https://travis-ci.com/jaime29010/LobbyBalancer.svg?token=2yUi9WpA9QzSbJx9eTmy&branch=master)](https://travis-ci.com/jaime29010/LobbyBalancer)

### Things to do:
- [x] Get dummy sections able to have already registered servers on other sections
- [ ] Make `marker-descs` work per section
- [ ] Unify the code that loads server into a section (duplicated at SectionManager and ServerSection) 
- [ ] Unify some of the code used in the FallbackCommand and SectionCommand
- [ ] Make the way of matching a string configurable (wildcard, contains, similar, regex)
- [ ] Make the section initialization work in stages instead of being hardcoded
- [ ] Ditch the faucet dependency and use [ConfigMe](https://github.com/AuthMe/ConfigMe) and [DependencyInjector](https://github.com/ljacqu/DependencyInjector) instead
- [ ] Use a separate file for configuring the sections, must be done alongside the forth item
- [ ] Separate the types of connections in classes instead of being in ConnectionIntent
- [ ] Make the plugin API be not so dependent on a instance of LobbyBalancer
- [ ] Separate connection providers in classes instead of being hardcoded in an enum
- [ ] Make this repository public