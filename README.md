# LobbyBalancer
[Spigot Resource](https://www.spigotmc.org/resources/10788/)

[![Build Status](https://travis-ci.com/jaime29010/LobbyBalancer.svg?token=2yUi9WpA9QzSbJx9eTmy&branch=master)](https://travis-ci.com/jaime29010/LobbyBalancer)

### Things to do:
- [x] Get dummy sections able to have already registered servers on other sections
- [x] Add a new message for when a player gets connected to a server and repurpose the connecting one
- [ ] Add support for wildcards, contains, equalsIgnoreCase and regex at the same time
- [ ] Add a identifier to get the servers of a section (auto complete)
- [ ] Add tooltip when you hover over a server in /section info
- [ ] Create a LobbyBalancer spigot addon that adds connector signs and placeholders
- [ ] Separate the types of connections in classes instead of being in ConnectionIntent
- [ ] Make the plugin API not be so dependent on a instance of LobbyBalancer
- [ ] Separate connection providers in classes instead of being hardcoded in an enum
- [ ] Make the feature `marker-descs` work per section
- [ ] Unify the code that loads server into a section (duplicated at SectionManager and ServerSection) 
- [ ] Unify some of the code used in the FallbackCommand and SectionCommand
- [ ] Use https://github.com/kennedyoliveira/pastebin4j instead of jpaste
- [ ] (!) Make the section initialization work in stages instead of being hardcoded
- [ ] (!) Ditch the faucet dependency and use [ConfigMe](https://github.com/AuthMe/ConfigMe) and [DependencyInjector](https://github.com/ljacqu/DependencyInjector) instead
- [ ] Use a separate file for configuring the sections, must be done alongside the previous item
- [ ] Make this repository public