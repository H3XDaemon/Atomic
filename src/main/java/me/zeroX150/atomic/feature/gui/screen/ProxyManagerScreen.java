package me.zeroX150.atomic.feature.gui.screen;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.feature.gui.clickgui.ClickGUI;
import me.zeroX150.atomic.helper.Renderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class ProxyManagerScreen extends Screen {
    public static Proxy currentProxy = null;
    static boolean isSocks4 = true;
    Screen parent;
    TextFieldWidget actualProxy;
    TextFieldWidget username;
    TextFieldWidget password;

    public ProxyManagerScreen(Screen parent) {
        super(Text.of(""));
        this.parent = parent;
    }

    boolean isValid(String n) {
        String[] split = n.split(":");
        if (split.length != 2) return false;
        if (!StringUtils.isNumeric(split[1])) return false;
        int port = Integer.parseInt(split[1]);
        return port >= 1 && port <= 0xFFFF;
    }

    int getW() {
        return (int) Math.floor(width / 2d);
    }

    int getH() {
        return (int) Math.floor(height / 2d);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }

    @Override
    public void onClose() {
        Atomic.client.openScreen(parent);
    }

    @Override
    protected void init() {
        actualProxy = new TextFieldWidget(textRenderer, getW() - 90, getH() - 55, 180, 20, Text.of("SPECIAL:Proxy IP:PORT"));
        actualProxy.setMaxLength(100);
        addDrawableChild(actualProxy);
        username = new TextFieldWidget(textRenderer, getW() - 90, getH() - 30, 180, 20, Text.of("SPECIAL:Username"));
        username.setMaxLength(100);
        addDrawableChild(username);
        password = new TextFieldWidget(textRenderer, getW() - 90, getH() - 5, 180, 20, Text.of("SPECIAL:Password"));
        password.setMaxLength(100);
        addDrawableChild(password);
        if (currentProxy != null) {
            actualProxy.setText(currentProxy.ipPort);
            username.setText(currentProxy.username);
            password.setText(currentProxy.password);
        }
        ButtonWidget type = new ButtonWidget(getW() - 90, getH() + 20, 180, 20, Text.of("Type: " + (isSocks4 ? "SOCKS4" : "SOCKS5")), button -> {
            isSocks4 = !isSocks4;
            button.setMessage(Text.of("Type: " + (isSocks4 ? "SOCKS4" : "SOCKS5")));
            password.setEditable(!isSocks4);
        });
        password.setEditable(!isSocks4);
        addDrawableChild(type);
        ButtonWidget check = new ButtonWidget(getW() + 5, getH() + 45, 85, 20, Text.of("OK"), button -> {
            boolean validProxy = isValid(actualProxy.getText());
            if (!validProxy) {
                actualProxy.setEditableColor(new Color(255, 20, 20).getRGB());
                return;
            } else actualProxy.setEditableColor(0xFFFFFF);
            System.out.println(actualProxy.getText());
            currentProxy = new Proxy(isSocks4, actualProxy.getText(), username.getText(), password.getText());
        });
        addDrawableChild(check);
        ButtonWidget cancel = new ButtonWidget(getW() - 90, getH() + 45, 85, 20, Text.of("Reset"), button -> {
            currentProxy = null;
        });
        addDrawableChild(cancel);

    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        parent.render(matrices, mouseX, mouseY, delta);
        DrawableHelper.fill(matrices, getW() - 100, getH() - 75, getW() + 100, getH() + 75, Renderer.modify(ClickGUI.currentActiveTheme.inactive(), -1, -1, -1, 60).getRGB());
        Atomic.fontRenderer.drawCenteredString(matrices, "Proxy manager" + (currentProxy == null ? "" : " (Using proxy)"), getW(), getH() - 70, 0xFFFFFF);
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(0, 0, 0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static class Proxy {
        public String ipPort;
        public ProxyType type;
        public String username;
        public String password;

        public Proxy(boolean isSocks4, String ipPort, String username, String password) {
            this.type = isSocks4 ? ProxyType.SOCKS4 : ProxyType.SOCKS5;
            this.ipPort = ipPort;
            this.username = username.isEmpty() ? null : username;
            this.password = password.isEmpty() ? null : password;
        }

        public int getPort() {
            return Integer.parseInt(ipPort.split(":")[1]);
        }

        public String getIp() {
            return ipPort.split(":")[0];
        }

        public enum ProxyType {
            SOCKS4,
            SOCKS5
        }
    }
}
