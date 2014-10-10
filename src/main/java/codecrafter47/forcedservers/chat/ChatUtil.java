package codecrafter47.forcedservers.chat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    static Pattern pattern = Pattern.compile(
            "(?:(?<!\\\\)(?:(?!(?:[*_]{2,2}) )|(?<! ))(?<strong>[*_]{2,2}))|"
                    + "(?:(?<!\\\\)(?:(?![*_] )|(?<! ))(?<em>[*_]))|"
                    + "(?:(?<!\\\\)[§&](?<color>[" + ChatColor.ALL_CODES + "]))|"
                    + "(?:(?<!\\\\)<(?<directlink>[^<>]+)>)|"
                    + "(?:(?<!\\\\)\\[(?<text>[^\\]]*?)(?<!\\\\)\\](?=[({])(?:\\((?<url>.*?)(?<!\\\\)\\))?(?:\\{(?<tooltip>.*?)(?<!\\\\)\\})?)|"
                    + "(?:(?<!\\\\)\\[(?<cmdtext>[^\\]]*?)(?<!\\\\)\\](?=[\\[{])(?:\\[(?<cmdurl>.*?)(?<!\\\\)\\])?(?:\\{(?<cmdtooltip>.*?)(?<!\\\\)\\})?)"
    );

    public static BaseComponent[] parseString(String s) {
        return iparseString(new ComponentBuilder(""), s).create();
    }

    public static ComponentBuilder iparseString(ComponentBuilder cb, String s) {

        Matcher matcher = pattern.matcher(s);
        //int i = 0;
        boolean bold = false;
        boolean italic = false;

        while (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            matcher.appendReplacement(sb, "");
            String str = sb.toString(); // TODO remove escapes
            str = removeEscapes(str);
            cb = cb.append(str);
            cb = cb.append("");

            if (matcher.group("strong") != null) {
                cb = cb.bold(bold = !bold);
            }
            if (matcher.group("em") != null) {
                cb = cb.italic(italic = !italic);
            }
            if (matcher.group("color") != null) {
                cb = cb.color(ChatColor.getByChar(matcher.group("color").charAt(
                        0)));
            }
            if (matcher.group("text") != null) {
                String url = matcher.group("url");
                String hover = matcher.group("tooltip");
                cb = cb.underlined(true);
                if (url != null) {
                    ClickEvent evt;
                    if (url.charAt(0) == '/') {
                        evt = new ClickEvent(ClickEvent.Action.RUN_COMMAND, url);
                    } else {
                        evt = new ClickEvent(ClickEvent.Action.OPEN_URL,
                                makeLink(url));
                    }
                    cb = cb.event(evt);
                }
                if (hover != null) {
                    cb = cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            parseString(hover)));
                }
                cb = iparseString(cb, matcher.group("text"));
                cb = cb.append("");
                cb = cb.event((ClickEvent) null);
                cb = cb.event((HoverEvent) null);
                cb = cb.underlined(false);
            }
            if (matcher.group("cmdtext") != null) {
                String url = matcher.group("cmdurl");
                String hover = matcher.group("cmdtooltip");
                cb = cb.underlined(true);
                if (url != null) {
                    ClickEvent evt;
                    if (url.charAt(0) == '/') {
                        evt = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                                url);
                    } else {
                        evt = new ClickEvent(ClickEvent.Action.OPEN_URL,
                                makeLink(url));
                    }
                    cb = cb.event(evt);
                }
                if (hover != null) {
                    cb = cb.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            parseString(hover)));
                }
                cb = iparseString(cb, matcher.group("cmdtext"));
                cb = cb.append("");
                cb = cb.event((ClickEvent) null);
                cb = cb.event((HoverEvent) null);
                cb = cb.underlined(false);
            }
            if (matcher.group("directlink") != null) {
                cb = cb.underlined(true);
                cb = cb.event(new ClickEvent(ClickEvent.Action.OPEN_URL,
                        makeLink(matcher.group("directlink"))));
                cb = cb.append(matcher.group("directlink"));
                cb = cb.append("");
                cb = cb.event((ClickEvent) null);
                cb = cb.event((HoverEvent) null);
                cb = cb.underlined(false);

            }
        }

        StringBuffer sb = new StringBuffer();
        matcher.appendTail(sb);
        cb = cb.append(sb.toString());

        return cb;
    }

    private static String removeEscapes(String str) {
        Pattern escapePattern = Pattern.compile(
                "\\\\(?<rep>[\\[\\]()<>*{}§&\\\\])");
        Matcher matcher = escapePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group("rep"));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static String makeLink(String link) {
        if (!link.matches("http((s)?)://.*")) {
            return "http://" + link;
        }
        return link;
    }
}
