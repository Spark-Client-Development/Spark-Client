package me.wallhacks.spark.gui.panels;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import me.wallhacks.spark.Spark;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;
import me.wallhacks.spark.manager.FontManager;
import me.wallhacks.spark.systems.clientsetting.clientsettings.GuiSettings;
import me.wallhacks.spark.util.objects.Timer;

public class GuiPanelInputField extends GuiPanelBase {


    public GuiPanelInputField(int id, int posX, int posY, int width, int height, int backGround, int text) {
        super(posX, posY, width, height);
        backGroundColor = backGround;
        textColor = text;

        this.id = id;
        fontRenderer = Spark.fontManager;
    }

    public GuiPanelInputField(int id,int posX, int posY, int width, int height) {
        super(posX, posY, width, height);
        backGroundColor = GuiSettings.getInstance().getGuiSubPanelBackgroundColor().getRGB();
        textColor = GuiSettings.getInstance().getContrastColor().getRGB();
        this.id = id;
        fontRenderer = Spark.fontManager;
    }
    int backGroundColor;
    int textColor;
    private final int id;
    private FontManager fontRenderer;
    private String text = "";
    private int maxStringLength = 32;
    private boolean isPassword = false;
    private Timer timer = new Timer();

    public void setPassword(boolean password) {
        isPassword = password;
    }

    private boolean isEnabled = true;
    private int lineScrollOffset;
    private int cursorPosition;
    private int selectionEnd;
    int textOffsetX = 4;


    private GuiPageButtonList.GuiResponder guiResponder;
    private Predicate<String> validator = Predicates.alwaysTrue();


    public void setGuiResponder(GuiPageButtonList.GuiResponder guiResponderIn) {
        this.guiResponder = guiResponderIn;
    }

    public void setFontRenderer(FontManager fontManager) {
        this.fontRenderer = fontManager;
    }

    public boolean fucked() {return fontRenderer == null; }


    public void setText(String textIn) {
        if (this.validator.apply(textIn)) {
            if (textIn.length() > this.maxStringLength) {
                this.text = textIn.substring(0, this.maxStringLength);
            } else {
                this.text = textIn;
            }

            if(!isFocused())
                this.setCursorPosition(0);
            this.setCursorPositionEnd();


        }

    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBackGroundColor(int backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        return this.text.substring(i, j);
    }

    public void setValidator(Predicate<String> theValidator) {
        this.validator = theValidator;
    }

    public void writeText(String textToWrite) {
        String s = "";
        String s1 = ChatAllowedCharacters.filterAllowedCharacters(textToWrite);
        int i = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
        int j = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
        int k = this.maxStringLength - this.text.length() - (i - j);
        if (!this.text.isEmpty()) {
            s = s + this.text.substring(0, i);
        }

        int l;
        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (!this.text.isEmpty() && j < this.text.length()) {
            s = s + this.text.substring(j);
        }

        if (this.validator.apply(s)) {
            this.text = s;
            this.moveCursorBy(i - this.selectionEnd + l);
            this.setResponderEntryValue(this.id, this.text);
        }

    }

    public void setResponderEntryValue(int idIn, String textIn) {
        if (this.guiResponder != null) {
            this.guiResponder.setEntryValue(idIn, textIn);
        }

    }

    public void deleteWords(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                this.deleteFromCursor(this.getNthWordFromCursor(num) - this.cursorPosition);
            }
        }

    }

    public void deleteFromCursor(int num) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.cursorPosition) {
                this.writeText("");
            } else {
                boolean flag = num < 0;
                int i = flag ? this.cursorPosition + num : this.cursorPosition;
                int j = flag ? this.cursorPosition : this.cursorPosition + num;
                String s = "";
                if (i >= 0) {
                    s = this.text.substring(0, i);
                }

                if (j < this.text.length()) {
                    s = s + this.text.substring(j);
                }

                if (this.validator.apply(s)) {
                    this.text = s;
                    if (flag) {
                        this.moveCursorBy(num);
                    }

                    this.setResponderEntryValue(this.id, this.text);
                }
            }
        }

    }

    public int getNthWordFromCursor(int numWords) {
        return this.getNthWordFromPos(numWords, this.getCursorPosition());
    }

    public int getNthWordFromPos(int n, int pos) {
        return this.getNthWordFromPosWS(n, pos, true);
    }

    public int getNthWordFromPosWS(int n, int pos, boolean skipWs) {
        int i = pos;
        boolean flag = n < 0;
        int j = Math.abs(n);

        for(int k = 0; k < j; ++k) {
            if (!flag) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while(skipWs && i < l && this.text.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while(skipWs && i > 0 && this.text.charAt(i - 1) == ' ') {
                    --i;
                }

                while(i > 0 && this.text.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursorBy(int num) {
        this.setCursorPosition(this.selectionEnd + num);
    }

    public void setCursorPosition(int pos) {
        this.cursorPosition = pos;
        int i = this.text.length();
        this.cursorPosition = MathHelper.clamp(this.cursorPosition, 0, i);
        this.setSelectionPos(this.cursorPosition);
    }

    public void setCursorPositionZero() {
        this.setCursorPosition(0);
    }

    public void setCursorPositionEnd() {
        this.setCursorPosition(this.text.length());
    }


    @Override
    public void onKey(int keyCode, char typedChar) {
        super.onKey(keyCode,typedChar);

        if (isFocused()) {
            if (keyCode == Keyboard.KEY_RETURN) {
                setFocused(false);
            } else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
                this.setCursorPositionEnd();
                this.setSelectionPos(0);
                return;
            } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
                GuiScreen.setClipboardString(this.getSelectedText());
                return;
            } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
                if (this.isEnabled) {
                    this.writeText(GuiScreen.getClipboardString());
                }

                return;
            } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
                GuiScreen.setClipboardString(this.getSelectedText());
                if (this.isEnabled) {
                    this.writeText("");
                }

                return;
            } else {
                switch(keyCode) {
                    case 14:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.isEnabled) {
                                this.deleteWords(-1);
                            }
                        } else if (this.isEnabled) {
                            this.deleteFromCursor(-1);
                        }

                        return;
                    case 199:
                        if (GuiScreen.isShiftKeyDown()) {
                            this.setSelectionPos(0);
                        } else {
                            this.setCursorPositionZero();
                        }

                        return;
                    case 203:
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                            } else {
                                this.setSelectionPos(this.getSelectionEnd() - 1);
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            this.setCursorPosition(this.getNthWordFromCursor(-1));
                        } else {
                            this.moveCursorBy(-1);
                        }

                        return;
                    case 205:
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                            } else {
                                this.setSelectionPos(this.getSelectionEnd() + 1);
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            this.setCursorPosition(this.getNthWordFromCursor(1));
                        } else {
                            this.moveCursorBy(1);
                        }

                        return;
                    case 207:
                        if (GuiScreen.isShiftKeyDown()) {
                            this.setSelectionPos(this.text.length());
                        } else {
                            this.setCursorPositionEnd();
                        }

                        return;
                    case 211:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.isEnabled) {
                                this.deleteWords(1);
                            }
                        } else if (this.isEnabled) {
                            this.deleteFromCursor(1);
                        }

                        return;
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                            if (this.isEnabled) {
                                timer.reset();
                                this.writeText(Character.toString(typedChar));
                            }
                            return;
                        } else {
                            return;
                        }
                }
            }
        }
    }

    @Override
    public void onClickDown(int mouseButton, int mouseX, int mouseY) {
        super.onClickDown(mouseButton, mouseX, mouseY);




        if (this.isFocused() && mouseButton == 0) {
            int i = mouseX - this.posX;

            i -= textOffsetX;



            String s = this.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
            this.setCursorPosition(this.trimStringToWidth(s, i).length() + this.lineScrollOffset);

        }
    }

    public String trimStringToWidth(String text, int width) {
        return this.trimStringToWidth(text, width, false);
    }

    public String trimStringToWidth(String text, int width, boolean reverse) {
        StringBuilder stringbuilder = new StringBuilder();
        int i = 0;
        int j = reverse ? text.length() - 1 : 0;
        int k = reverse ? -1 : 1;
        boolean flag = false;
        boolean flag1 = false;

        for(int l = j; l >= 0 && l < text.length() && i < width; l += k) {
            char c0 = text.charAt(l);
            int i1 = fontRenderer.getTextWidth(c0+"");
            if (flag) {
                flag = false;
                if (c0 != 'l' && c0 != 'L') {
                    if (c0 == 'r' || c0 == 'R') {
                        flag1 = false;
                    }
                } else {
                    flag1 = true;
                }
            } else if (i1 < 0) {
                flag = true;
            } else {
                i += i1;
                if (flag1) {
                    ++i;
                }
            }

            if (i > width) {
                break;
            }

            if (reverse) {
                stringbuilder.insert(0, c0);
            } else {
                stringbuilder.append(c0);
            }
        }

        return stringbuilder.toString();
    }

    public void setTextOffsetX(int textOffsetX){
        this.textOffsetX = textOffsetX;

    }

    @Override
    public void renderContent(int MouseX, int MouseY, float deltaTime) {

        String rendertext = text;

        if(isPassword)
        {
            rendertext = "";
            for (int i = 0; i < text.length(); i++) {
                if (i == text.length() - 1 && !timer.passedMs(500)) {
                    rendertext+=text.charAt(i);
                } else rendertext+="*";
            }
        }

        drawBackGround(backGroundColor);


        int j = this.cursorPosition - this.lineScrollOffset;
        int k = this.selectionEnd - this.lineScrollOffset;
        String s = this.trimStringToWidth(rendertext.substring(this.lineScrollOffset), this.getWidth());
        boolean flag = j >= 0 && j <= s.length();
        boolean flag1 = this.isFocused() && (int)(System.currentTimeMillis()/50) / 6 % 2 == 0 && flag;
        int l = this.posX + textOffsetX;
        int i1 = this.posY + (this.height - 8) / 2;
        int j1 = l;
        if (k > s.length()) {
            k = s.length();
        }

        if (!s.isEmpty()) {
            String s1 = flag ? s.substring(0, j) : s;
            j1 = l+this.fontRenderer.getTextWidth(s1);
            this.fontRenderer.drawString(s, l, i1+1, textColor);
        }

        boolean flag2 = this.cursorPosition < rendertext.length() || rendertext.length() >= this.getMaxStringLength();
        int k1 = j1;
        if (!flag) {
            k1 = j > 0 ? l + this.width : l;
        } else if (flag2) {
            k1 = j1 - 1;
        }


        if (flag1) {
            if (flag2) {
                Gui.drawRect(k1+1, i1 - 1, k1 + 2, i1 + 1 + this.fontRenderer.getTextHeight(), textColor);
            } else {
                this.fontRenderer.drawString("_", k1, i1+1, textColor);
            }
        }

        if (k != j) {
            int l1 = l + this.fontRenderer.getTextWidth(s.substring(0, k));
            this.drawSelectionBox(k1, i1 - 1, l1 - 1, i1 + 1 + this.fontRenderer.getTextHeight());
        }
        super.renderContent(MouseX, MouseY, deltaTime);
    }

    private void drawSelectionBox(int startX, int startY, int endX, int endY) {
        int j;
        if (startX < endX) {
            j = startX;
            startX = endX;
            endX = j;
        }

        if (startY < endY) {
            j = startY;
            startY = endY;
            endY = j;
        }

        if (endX > this.posX + this.width) {
            endX = this.posX + this.width;
        }

        if (startX > this.posX + this.width) {
            startX = this.posX + this.width;
        }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)startX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)endY, 0.0D).endVertex();
        bufferbuilder.pos((double)endX, (double)startY, 0.0D).endVertex();
        bufferbuilder.pos((double)startX, (double)startY, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setMaxStringLength(int length) {
        this.maxStringLength = length;
        if (this.text.length() > length) {
            this.text = this.text.substring(0, length);
        }

    }

    public int getMaxStringLength() {
        return this.maxStringLength;
    }

    public int getCursorPosition() {
        return this.cursorPosition;
    }






    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    int getSelectionEnd() {
        return this.selectionEnd;
    }

    int getWidth() {
        return this.width - 8;
    }

    public void setSelectionPos(int position) {
        int i = this.text.length();
        if (position > i) {
            position = i;
        }

        if (position < 0) {
            position = 0;
        }

        this.selectionEnd = position;
        if (this.fontRenderer != null) {
            if (this.lineScrollOffset > i) {
                this.lineScrollOffset = i;
            }

            int j = this.getWidth();
            String s = this.trimStringToWidth(this.text.substring(this.lineScrollOffset), j);
            int k = s.length() + this.lineScrollOffset;
            if (position == this.lineScrollOffset) {
                this.lineScrollOffset -= this.trimStringToWidth(this.text, j, true).length();
            }

            if (position > k) {
                this.lineScrollOffset += position - k;
            } else if (position <= this.lineScrollOffset) {
                this.lineScrollOffset -= this.lineScrollOffset - position;
            }

            this.lineScrollOffset = MathHelper.clamp(this.lineScrollOffset, 0, i);
        }

    }


}
