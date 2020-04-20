#Core
This module contains the core functions and features of Project Lava.

These functions and features are in moe.ofs.backend package.

*moe.ofs.backend.function*
contains classes that reflects a scripting "action" in DCS World. For example, adding a group to the environment, delete a User Mark Panel from tactical map, or send a trigger message to a specific group, and so on.

*moe.ofs.backend.handlers*
contains functional interfaces that can be used as an event. You can implement those interface via lambda expression and provide an EventHandler-ish behavior upon certain events such as when a player spawn, when mission start, when a new AI unit is added to the sim, or when a player has changed to a new slot, etc.

*moe.ofs.backend.request*
contains the core network code that handles JSON RPC with local Lua server embedded in Hook and Export environment.

*moe.ofs.backend.services*
contains data services that interact with repositories located in Data module. These services provide realtime data on players and objects, as well as some common static data sets predefined by Project Lava.

*moe.ofs.backend.util*
contains utility classes that can be used to simplify localization and Lua script loading.
