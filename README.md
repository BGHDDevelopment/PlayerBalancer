# PlayerBalancer
[Spigot Resource](https://www.spigotmc.org/resources/10788/)

[![CircleCI](https://circleci.com/gh/Jamezrin/PlayerBalancer.svg?style=svg&circle-token=8cda98f6953e0370e552f5eecff8d3f13b116ab9)](https://circleci.com/gh/Jamezrin/PlayerBalancer)

### Build
* Clone this repository
* Run the appropriate setup script according to your OS
* Build with maven

### Things to do:
- [ ] Create a spigot addon that adds connector signs and placeholders
- [ ] Separate the types of connections in classes instead of being in ConnectionIntent
- [ ] Make the plugin API be not so dependent on a instance of PlayerBalancer
- [ ] Separate connection providers in classes instead of being hardcoded in an enum
- [ ] Make the feature `marker-descs` work per section
- [ ] Implement fast connect (dimension change)
- [ ] Implement a way to redirect premium players to a section and cracked ones to other section (not sure how this works)
- [x] Unify the code that loads serverName into a section (duplicated at SectionManager and ServerSection) 
- [x] Unify some of the code used in the FallbackCommand and SectionCommand
- [x] Make the section initialization work in stages instead of being hardcoded