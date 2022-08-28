package dev.proxyfox.bot.command

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.message.ReactionAddEvent
import dev.kord.core.on
import dev.kord.rest.builder.message.create.embed
import dev.proxyfox.bot.kord
import dev.proxyfox.bot.string.dsl.greedy
import dev.proxyfox.bot.string.dsl.literal
import dev.proxyfox.bot.string.parser.MessageHolder
import dev.proxyfox.bot.string.parser.registerCommand
import dev.proxyfox.common.printStep
import dev.proxyfox.database.database
import kotlinx.coroutines.Job
import java.time.format.DateTimeFormatter

/**
 * Commands for accessing and changing system settings
 * @author Oliver
 * */
object SystemCommands {
    suspend fun register() {
        printStep("Registering system commands", 2)
        val system: CommandNode = {
            val new: CommandNode = {
                greedy("name", ::create)
            }
            literal("new", ::createEmpty, new)
            literal("n", ::createEmpty, new)
            literal("create", ::createEmpty, new)
            literal("add", ::createEmpty, new)

            val name: CommandNode = {
                greedy("name", ::rename)
            }
            literal("rename", ::renameEmpty, name)
            literal("name", ::accessName, name)

            val list: CommandNode = {
                literal("-by-message-count", ::listByMessage)
            }
            literal("list", ::list, list)
            literal("l", ::list, list)

            val desc: CommandNode = {
                literal("-raw", ::descriptionRaw)
                greedy("desc", ::description)
            }
            literal("description", ::descriptionEmpty, desc)
            literal("desc", ::descriptionEmpty, desc)
            literal("d", ::descriptionEmpty, desc)

            val avatar: CommandNode = {
                literal("-raw", ::avatarRaw)
                literal("-clear", ::avatarClear)
                literal("-delete", ::avatarClear)
                greedy("avatar", ::avatar)
            }
            literal("avatar", ::avatarEmpty, avatar)
            literal("pfp", ::avatarEmpty, avatar)

            literal("tag", ::tagEmpty) {
                literal("-raw", ::tagRaw)
                literal("-clear", ::tagClear)
                literal("-delete", ::tagClear)
                greedy("tag", ::tag)
            }

            literal("delete", ::delete)
        }
        registerCommand(literal("system", ::empty, system))
        registerCommand(literal("s", ::empty, system))
    }

    private suspend fun empty(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        val members = database.getTotalMembersByHost(ctx.message.author)
        ctx.message.channel.createMessage {
            embed {
                title = "${system.name} [`${system.id}`]"
                val avatar = system.avatarUrl
                if (avatar != null) thumbnail {
                    url = avatar
                }
                val tag = system.tag
                if (tag != null) field {
                    name = "Tag"
                    value = tag
                    inline = true
                }
                field {
                    name = "Members (`${members}`)"
                    value = "See `pf>system list`"
                    inline = true
                }
                val description = system.description
                if (description != null) field {
                    name = "Description"
                    value = description
                }
                footer {
                    val formatter = DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss")
                    text = "Created on ${formatter.format(system.timestamp)}"
                }
            }
        }
        return ""
    }

    private suspend fun createEmpty(ctx: MessageHolder): String {
        database.allocateSystem(ctx.message.author!!)
        return "System created! See `pf>help` for how to set up your system further!"
    }

    private suspend fun create(ctx: MessageHolder): String {
        val system = database.allocateSystem(ctx.message.author!!)
        system.name = ctx.params["name"]!![0]
        database.updateSystem(system)
        return "System created with name ${system.name}! See `pf>help` for how to set up your system further!"
    }

    private suspend fun renameEmpty(ctx: MessageHolder): String {
        database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return "Make sure to provide me with a name to update your system!"
    }

    private suspend fun rename(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        system.name = ctx.params["name"]!![0]
        database.updateSystem(system)
        return "System name updated to ${system.name}!"
    }

    private suspend fun accessName(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return "System's name is ${system.name}"
    }

    private suspend fun list(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"

        return ""
    }

    private suspend fun listByMessage(ctx: MessageHolder): String {
        // TODO: Make it sort by message count
        return list(ctx)
    }

    private suspend fun description(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        system.description = ctx.params["desc"]!![0]
        database.updateSystem(system)
        return "Description updated!"
    }

    private suspend fun descriptionRaw(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return "```md\n${system.description}```"
    }

    private suspend fun descriptionEmpty(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return system.description ?: "Description not set."
    }

    private suspend fun avatar(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        system.avatarUrl = ctx.params["avatar"]!![0]
        database.updateSystem(system)
        return "System avatar updated!"
    }

    private suspend fun avatarClear(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        system.avatarUrl = null
        database.updateSystem(system)
        return "System avatar cleared!"
    }

    private suspend fun avatarRaw(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return "`${system.avatarUrl}`"
    }

    private suspend fun avatarEmpty(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return system.avatarUrl ?: "System avatar not set."
    }

    private suspend fun tag(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        system.tag = ctx.params["tag"]!![0]
        database.updateSystem(system)
        return "System tag updated!"
    }

    private suspend fun tagClear(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        system.tag = null
        database.updateSystem(system)
        return "System tag cleared!"
    }

    private suspend fun tagRaw(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return "`${system.tag}`"
    }

    private suspend fun tagEmpty(ctx: MessageHolder): String {
        val system = database.getSystemByHost(ctx.message.author)
            ?: return "System does not exist. Create one using `pf>system new`"
        return system.tag ?: "System tag not set."
    }

    private suspend fun delete(ctx: MessageHolder): String {
        val author = ctx.message.author!!
        database.getSystemByHost(author)
            ?: return "System does not exist. Create one using `pf>system new`"
        val message1 =
            ctx.message.channel.createMessage("Are you sure you want to delete your system?\nThe data will be lost forever (A long time!)")
        message1.addReaction(ReactionEmoji.Unicode("❌"))
        message1.addReaction(ReactionEmoji.Unicode("✅"))
        var job: Job? = null
        job = kord.on<ReactionAddEvent> {
            if (messageId == message1.id && userId == author.id) {
                when (emoji.name) {
                    "✅" -> {
                        database.removeSystem(author)
                        channel.createMessage("System deleted")
                        job!!.cancel()
                    }
                    "❌" -> {
                        channel.createMessage("Action cancelled")
                        job!!.cancel()
                    }
                }
            }
        }
        return ""
    }
}