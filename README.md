# PlayerBalancer
[Spigot Resource](https://www.spigotmc.org/resources/10788/)

[![Build Status](https://travis-ci.com/Jamezrin/PlayerBalancer.svg?token=2yUi9WpA9QzSbJx9eTmy&branch=master)](https://travis-ci.com/Jamezrin/PlayerBalancer)

### Things to do:
- [x] Get dummy sections able to have already registered serverEntries on other sections
- [x] Add a new message for when a player gets connected to a serverName and repurpose the connecting one
- [ ] Add support for wildcards, contains, equalsIgnoreCase and regex at the same time
- [ ] Add option to force joining a specific section (to the command)
- [x] Add tooltip when you hover over a serverName in /section info
- [ ] Stop using inventivetalent's deprecated bungee-update
- [ ] Create a spigot addon that adds connector signs and placeholders
- [x] Separate the types of connections in classes instead of being in ConnectionIntent
- [ ] Make the plugin API be not so dependent on a instance of PlayerBalancer
- [ ] Separate connection providers in classes instead of being hardcoded in an enum
- [ ] Make the feature `marker-descs` work per section
- [ ] Add a identifier to get the serverEntries of a section (auto complete)
- [ ] Implement fast connect (dimension change)
- [ ] Implement a way to redirect premium players to a section and cracked ones to other section (not sure how this works)
- [ ] Unify the code that loads serverName into a section (duplicated at SectionManager and ServerSection) 
- [ ] Unify some of the code used in the FallbackCommand and SectionCommand
- [ ] Make the section initialization work in stages instead of being hardcoded