package net.guizhanss.slimefunextension.core.services;

import java.text.MessageFormat;
import java.util.Locale;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.base.Preconditions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.guizhanss.guizhanlib.minecraft.utils.ChatUtil;
import net.guizhanss.guizhanlib.slimefun.addon.SlimefunLocalization;
import net.guizhanss.guizhanlib.utils.StringUtil;
import net.guizhanss.slimefunextension.SlimefunExtension;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

@SuppressWarnings("ConstantConditions")
public final class LocalizationService extends SlimefunLocalization {

    private static final MiniMessage MM = MiniMessage.builder()
        .tags(TagResolver.builder()
            .resolver(StandardTags.color())
            .resolver(StandardTags.decorations())
            .resolver(StandardTags.gradient())
            .resolver(StandardTags.reset())
            .build())
        .build();

    private static final LegacyComponentSerializer LCS = LegacyComponentSerializer.builder()
        .hexColors()
        .useUnusualXRepeatedCharacterHexFormat()
        .build();

    public LocalizationService(SlimefunExtension plugin) {
        super(plugin);
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    @Override
    public String getString(String key) {
        Preconditions.checkArgument(key != null, "Key cannot be null");

        return LCS.serialize(MM.deserialize(super.getString(key)));
    }

    @ParametersAreNonnullByDefault
    @Nonnull
    public String getString(String key, Object... args) {
        return MessageFormat.format(getString(key), args);
    }

    @Nonnull
    public String getResearchName(@Nonnull String researchId) {
        Preconditions.checkArgument(researchId != null, "Research Id cannot be null");

        return getString("researches." + StringUtil.dehumanize(researchId).toLowerCase(Locale.ROOT));
    }

    @ParametersAreNonnullByDefault
    public void sendMessage(CommandSender sender, String messageKey, Object... args) {
        Preconditions.checkArgument(sender != null, "CommandSender cannot be null");
        Preconditions.checkArgument(messageKey != null, "Message key cannot be null");

        ChatUtil.send(sender, MessageFormat.format(getString("messages." + messageKey), args));
    }

    @ParametersAreNonnullByDefault
    public void sendActionbarMessage(Player p, String messageKey, Object... args) {
        Preconditions.checkArgument(p != null, "Player cannot be null");
        Preconditions.checkArgument(messageKey != null, "Message key cannot be null");

        String message = MessageFormat.format(getString("messages." + messageKey), args);

        BaseComponent[] components = TextComponent.fromLegacyText(ChatUtil.color(message));
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
    }
}
