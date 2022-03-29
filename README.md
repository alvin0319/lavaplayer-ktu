# lavaplayer-ktu

A library for LavaPlayer and JDA to manage lavaplayer easily.

[![Release](https://jitpack.io/v/alvin0319/lavaplayer-ktu.svg)](https://jitpack.io/#alvin0319/lavaplayer-ktu)

# Required Dependencies

* Kotlin 1.16.0/Java 11 or Higher
* JDA 5.0.0-alpha.9 or Higher

<!--
# Examples

* Kotlin

```kotlin
fun main(args: Array<String>) {
    val jda = JDABuilder().createDefault("TOKEN")
        .enableIntents(LavaPlayerFactory.requiredIntents)
        .enableCache(LavaPlayerFactory.requiredFlags)
        .setMemberPolicy(LavaPlayerFactory.memberPolicy)
        .build()

    val guild = jda.getGuildById(215123155)

    val playerInfo = LavaPlayerFactory.getPlayer(jda, guild)
}
```

* Java

```java
public class Main {
    public static void main(String[] args) throws IllegalStateException, LoginException {
        JDA jda = JDABuilder.createDefault("TOKEN")
                .enableIntents(LavaPlayerFactory.requiredIntents)
                .enableCache(LavaPlayerFactory.requiredFlags)
                .setMemberPolicy(LavaPlayerFactory.memberPolicy)
                .build();

        Guild guild = jda.getGuildById(215123155);

        PlayerInfo playerInfo = LavaPlayerFactory.getPlayer(jda, guild);
    }
}
```
-->