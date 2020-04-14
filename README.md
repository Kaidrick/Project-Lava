[![CircleCI](https://circleci.com/gh/Kaidrick/422d-backend-ui.svg?style=shield&circle-token=52645945daec19c9f2ca08f057236b9eea790d6b)](https://circleci.com/gh/Kaidrick/422d-backend-ui/tree/master)

# Project Lava
This piece of software is an experiment to explore the possibility of decouple some of the most common scripting behavior and services from DCS lua script. It is aimed to be the a framework where some of the most common functions such a sending a greeting message to a player on his/her spawn, or setting up a tanker or EWACS service that can automatically dispatch aircraft based on conditions.

Project Lava is designed with compatibility in mind: it is supposed to be used, without the necessity to modified existing mission, and compatible with any map theatres, missions or DCS lua scripts such as MIST / Moose Framework or external tools such as SimpleRadioStandalone and TacView, as long as their authors avoid lua table namespace used by Project Lava.

This "common" behaviors are usually written in Lua script and included in each mission; that is how DCS is supposed to work, and that is as much expendable as it can be, without pulling runtime data out of the sim environment.

What Project Lava provides is a Java based backend application that obtains data from a  Lua server hooked on DCS callbacks, and thus it can provide APIs and SPIs for a pluggable behaviors besides what core Project Lava can provide.

This application can be used with third party addons/plugins to expend its functionality, or it can be used along as a Lua environment explorer. The GUI provides a Lua Editor that can highlight syntax for the user, and can send the content through TCP connection as JSON RPC to the hooked Lua Server, as immediately get a result in either mission, server or export environment.

If you'd like to load Project Lava with plugins, you can create a `addons` directory under `%USERPROFILE%\Saved Games\Lava\`(if you have run Lava at least once, it will create a config directory for you just besides your DCS write paths) and run with below JVM options, where Spring Boot loader searches *.jar files from Project Lava config directory and loads classes into classpath:

`java -cp "backend-gui-0.0.1-SNAPSHOT.jar" -Dloader.path="%USERPROFILE%\Saved Games\Lava\addons" -Dloader.main=moe.ofs.backend.BackendApplication org.springframework.boot.loader.PropertiesLauncher`

At current stage, Lava uses Hypersonic2 (H2) in-memory database to manage sim runtime data from DCS. Following the initial release, an option will be available to allow user to connect to local database and persist the current mission state of the mission.

Some of the ideas for Project Lava are borrowed from other DCS project such as DCS-Witchcraft, and some of the prototype projects had been made during the years to exercise this idea; in the end, Java / Spring is chosen to be what Project Lava will be written in because of its complexity and the possibility of DCS moving to unix platforms after it adopts new Vulkan API.

If you have the intention to produce addons for Project Lava, you can check docs and example provided in backend-plugin module in the core project.

