package attendance.ui;

import java.awt.*;

/**
 * Centralised colour and font constants for the application UI.
 * Inspired by a clean "deep navy + amber accent" dashboard aesthetic.
 */
public class UITheme {

    // ── Palette ───────────────────────────────────────────────────────────────
    public static final Color BG_DARK       = new Color(15,  23,  42);   // main window bg
    public static final Color BG_PANEL      = new Color(22,  33,  62);   // card panels
    public static final Color BG_SIDEBAR    = new Color(10,  16,  30);   // sidebar bg
    public static final Color ACCENT        = new Color(251, 191,  36);  // amber accent
    public static final Color ACCENT_HOVER  = new Color(245, 158,  11);  // amber hover
    public static final Color SUCCESS       = new Color( 52, 211, 153);  // green / present
    public static final Color DANGER        = new Color(248,  113, 113); // red / absent
    public static final Color TEXT_PRIMARY  = new Color(241, 245, 249);  // near-white
    public static final Color TEXT_MUTED    = new Color(148, 163, 184);  // slate-400
    public static final Color BORDER        = new Color( 30,  41,  59);  // subtle border
    public static final Color TABLE_ALT_ROW = new Color( 20,  29,  50);  // zebra stripe

    // ── Fonts ─────────────────────────────────────────────────────────────────
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD,  22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD,  15);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO    = new Font("Consolas",  Font.PLAIN, 12);

    // ── Corner radius helper ──────────────────────────────────────────────────
    public static final int RADIUS = 10;

    private UITheme() {}
}
