==============
=   SYNTAX   =
==============

Every sign should look like this

   [SENSOR] x y
   extra

x:
    Length of the sensor, limited to a value between 1 and max-length 
    default: max-length
y:
    How long to keep up the output signal:
        -2:     until right click on sign
        -1:     until triggered next time
        0:      as long as triggered (default)
        > 0:    duration in seconds.
extra:  
    More conditions to apply, see "EXTRA"

=============
=   EXTRA   =
=============

SYNTAX
    There can be multiple conditions, each of them with a modifier.
    Each condition has the syntax:
        
        [+|-]<type>[:<value>]

    Multiple conditions are separated by spaces. Note that a new line at the
    sign is not read as a character, so conditions can span multiple lines. Add
    a space at the end of a line to separate the condition from the next line.

CONDITION TYPES
    *   Any living entity. If no conditions are specified, this will be used.

    t   Type. Can be one of these: 
        Animal, Mob, Player
        PLANNED: Cow, Pig, Sheep, Chicken, Spider, Skeleton, Zombie, Creeper

    g   Group. Selects every player that is part of the group with the name
        <value>

    p   Player. Selects the player with the name <value>. If no player name
        specified, this equals to "t:Player"

    c   Command. Selects every player with access to the command <value>. If no
        command specified, this equals to "t:Player"
    
MODIFIERS
    Every condition can be modified by one modifier:
    
    +   Condition NEEDS to be met
    -   Condition MUST NOT be met
        Condition CAN be met, the sensor will trigger if one if these is met
        (default)

EXAMPLES
    Trigger at every LivingEntity
    > *

    Trigger for players not in group "humans"
    > -g:humans

    Trigger for players in group "humans" only
    > g:humans

    Trigger for players in group "humans" but not for players in group "thief"
    > g:humans -g:thief

    Trigger for players "pete" and "steve"
    > p:pete p:steve

    Trigger for players in group "humans" but only if they are also in group
    "vip"
    > +g:humans +g:vip

    Trigger for mobs and for players that are not in the group "friendly"
    > -g:friendly t:mob

