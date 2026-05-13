package view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Theme {

    public static final Color BG           = new Color(0xFAFAFA);
    public static final Color WHITE        = Color.WHITE;
    public static final Color SIDEBAR_BG   = new Color(0xFFFFFF);
    public static final Color SIDEBAR_LINE = new Color(0xE8E8E8);
    public static final Color PRIMARY      = new Color(0x2563EB);  // blue
    public static final Color PRIMARY_LIGHT= new Color(0xEFF6FF);
    public static final Color SUCCESS      = new Color(0x16A34A);
    public static final Color DANGER       = new Color(0xDC2626);
    public static final Color WARNING      = new Color(0xD97706);
    public static final Color TEXT_DARK    = new Color(0x111827);
    public static final Color TEXT_MID     = new Color(0x6B7280);
    public static final Color TEXT_LIGHT   = new Color(0x9CA3AF);
    public static final Color BORDER       = new Color(0xE5E7EB);
    public static final Color ROW_ALT      = new Color(0xF9FAFB);
    public static final Color ROW_SELECTED = new Color(0xEFF6FF);
    public static final Color DETAIL_BG    = new Color(0xFFFFFF);

    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  20);
    public static final Font FONT_SECTION = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_NAV     = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_NAV_SEL = new Font("Segoe UI", Font.BOLD,  13);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_VALUE   = new Font("Segoe UI", Font.BOLD,  13);

    public static Border lineBorder() {
        return BorderFactory.createLineBorder(BORDER, 1, true);
    }
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        );
    }
    public static Border fieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)
        );
    }
    public static Border fieldBorderFocus() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(5, 9, 5, 9)
        );
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(PRIMARY);
        b.setForeground(WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setOpaque(true);
        return b;
    }

    public static JButton outlineButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(WHITE);
        b.setForeground(TEXT_DARK);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(7, 17, 7, 17)
        ));
        b.setOpaque(true);
        return b;
    }

    public static JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BODY);
        b.setBackground(new Color(0xFEF2F2));
        b.setForeground(DANGER);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        b.setOpaque(true);
        return b;
    }

    public static JTextField styledField() {
        JTextField f = new JTextField();
        f.setFont(FONT_BODY);
        f.setBorder(fieldBorder());
        f.setBackground(WHITE);
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { f.setBorder(fieldBorderFocus()); }
            public void focusLost(java.awt.event.FocusEvent e)   { f.setBorder(fieldBorder()); }
        });
        return f;
    }

    public static JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(FONT_BODY);
        c.setBackground(WHITE);
        c.setBorder(fieldBorder());
        return c;
    }

    public static JTextArea styledArea(int rows) {
        JTextArea a = new JTextArea(rows, 0);
        a.setFont(FONT_BODY);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        return a;
    }

    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SECTION);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MID);
        return l;
    }

    public static JLabel valueLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_VALUE);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(ROW_SELECTED);
        table.setSelectionForeground(TEXT_DARK);
        table.setBackground(WHITE);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SECTION);
        header.setBackground(BG);
        header.setForeground(TEXT_MID);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(0, 38));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                                                           boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                if (!sel) setBackground(row % 2 == 0 ? WHITE : ROW_ALT);
                setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
                setForeground(sel ? TEXT_DARK : (col == 0 ? TEXT_MID : TEXT_DARK));
                if (col == 0) setFont(FONT_SMALL);
                return this;
            }
        });
    }

    public static JLabel badge(String text, Color bg, Color fg) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setBackground(bg);
        l.setForeground(fg);
        l.setOpaque(true);
        l.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return l;
    }
}