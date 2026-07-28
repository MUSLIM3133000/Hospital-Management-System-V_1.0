package ui;

import model.Address;
import model.Doctor;
import model.Nurse;
import model.Patient;
import service.HospitalService;
import service.ManagementSystem;
import util.Validator;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Main GUI window for the Hospital Management System.
 *
 * Architecture: MVC
 *  - Model      : ManagementSystem (service layer behind HospitalService interface)
 *  - View       : this class (renders all panels, tables, dialogs)
 *  - Controller : ActionListeners + helper methods wired inside this class
 *
 * Visual language: dark "glass" theme — soft gradients, rounded cards,
 * gentle drop shadows and a violet/cyan/emerald accent palette.
 *
 * Compatible with Java 11+.
 */
public class HospitalGUI extends JFrame {

    // ── Design tokens ──────────────────────────────────────────────────────────
    private static final Color BG_DEEP        = new Color(0x0B0F1A);
    private static final Color BG_DEEP_2      = new Color(0x121A2C);
    private static final Color SIDEBAR_TOP    = new Color(0x161F35);
    private static final Color SIDEBAR_BOTTOM = new Color(0x0C1220);
    private static final Color CARD_BG        = new Color(0x161F35);
    private static final Color CARD_BG_HOVER  = new Color(0x1C273F);
    private static final Color BORDER_SUBTLE  = new Color(0x263151);
    private static final Color FIELD_BG       = new Color(0x1C2740);

    private static final Color PRIMARY       = new Color(0x6C5CE7);
    private static final Color PRIMARY_LIGHT = new Color(0x8E7CF7);
    private static final Color PRIMARY_DARK  = new Color(0x4A3BC4);
    private static final Color CYAN          = new Color(0x22D3EE);
    private static final Color EMERALD       = new Color(0x34D399);
    private static final Color AMBER         = new Color(0xFBBF24);
    private static final Color ORCHID        = new Color(0xC084FC);
    private static final Color ORANGE        = new Color(0xFB923C);
    private static final Color DANGER        = new Color(0xF43F5E);
    private static final Color DANGER_DARK   = new Color(0xC22E45);

    private static final Color TEXT_LIGHT = new Color(0xF1F5F9);
    private static final Color TEXT_MUTED = new Color(0x9AA7C2);
    private static final Color TEXT_DIM   = new Color(0x64708A);

    private static final Font FONT_HERO     = new Font("Segoe UI", Font.BOLD,  24);
    private static final Font FONT_TITLE    = new Font("Segoe UI", Font.BOLD,  21);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD,  14);
    private static final Font FONT_NORMAL   = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL    = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BUTTON   = new Font("Segoe UI", Font.BOLD,  12);
    private static final Font FONT_NUMBER   = new Font("Segoe UI", Font.BOLD,  36);
    private static final Font FONT_BRAND    = new Font("Segoe UI", Font.BOLD,  18);

    // ── Backend (injected through interface — testable / swappable) ────────────
    private final HospitalService service = new ManagementSystem();

    // ── Layout ─────────────────────────────────────────────────────────────────
    private JPanel     contentPanel;
    private CardLayout cardLayout;
    private final List<NavButton> navButtons = new ArrayList<>();

    // ── Table models (kept as fields so refresh methods can reach them) ────────
    private DefaultTableModel doctorTableModel;
    private DefaultTableModel nurseTableModel;
    private DefaultTableModel patientTableModel;
    private DefaultTableModel apptTableModel;

    // ── Building output ────────────────────────────────────────────────────────
    private JTextArea buildingTextArea;

    // ══════════════════════════════════════════════════════════════════════════
    //  Constructor
    // ══════════════════════════════════════════════════════════════════════════

    public HospitalGUI() {
        initWindow();
        buildLayout();
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Window Setup
    // ══════════════════════════════════════════════════════════════════════════

    private void initWindow() {
        setTitle("Paradise Hospital — Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_DEEP);
    }

    private void buildLayout() {
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);

        cardLayout   = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(BG_DEEP);

        contentPanel.add(buildDashboard(),        "DASHBOARD");
        contentPanel.add(buildDoctorPanel(),      "DOCTORS");
        contentPanel.add(buildNursePanel(),       "NURSES");
        contentPanel.add(buildPatientPanel(),     "PATIENTS");
        contentPanel.add(buildAppointmentPanel(), "APPOINTMENTS");
        contentPanel.add(buildBuildingPanel(),    "BUILDING");

        add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Sidebar Navigation
    // ══════════════════════════════════════════════════════════════════════════

    /** Sidebar panel painted with a subtle vertical gradient. */
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_TOP, 0, getHeight(), SIDEBAR_BOTTOM);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(BORDER_SUBTLE);
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(232, 0));

        // ── Logo block ─────────────────────────────────────────────────────────
        JPanel logoPanel = new JPanel(new BorderLayout(10, 0));
        logoPanel.setOpaque(false);
        logoPanel.setMaximumSize(new Dimension(232, 84));
        logoPanel.setPreferredSize(new Dimension(232, 84));
        logoPanel.setBorder(new EmptyBorder(16, 20, 16, 16));

        logoPanel.add(monogramBadge("PH", PRIMARY_LIGHT, PRIMARY_DARK, 44), BorderLayout.WEST);

        JLabel brand = new JLabel("Paradise Hospital");
        brand.setFont(FONT_BRAND);
        brand.setForeground(TEXT_LIGHT);

        JLabel sub = new JLabel("Management Suite");
        sub.setFont(FONT_SMALL);
        sub.setForeground(TEXT_MUTED);

        JPanel lt = new JPanel();
        lt.setLayout(new BoxLayout(lt, BoxLayout.Y_AXIS));
        lt.setOpaque(false);
        lt.add(Box.createVerticalGlue());
        lt.add(brand);
        lt.add(Box.createVerticalStrut(3));
        lt.add(sub);
        lt.add(Box.createVerticalGlue());
        logoPanel.add(lt, BorderLayout.CENTER);

        sidebar.add(logoPanel);
        sidebar.add(sectionDivider());
        sidebar.add(Box.createVerticalStrut(14));

        // ── Nav items ──────────────────────────────────────────────────────────
        addNavItem(sidebar, "Dashboard",         "DASHBOARD",    PRIMARY_LIGHT);
        addNavItem(sidebar, "Doctors",           "DOCTORS",      CYAN);
        addNavItem(sidebar, "Nurses",            "NURSES",       EMERALD);
        addNavItem(sidebar, "Patients",          "PATIENTS",     AMBER);
        addNavItem(sidebar, "Appointments",      "APPOINTMENTS", ORCHID);
        addNavItem(sidebar, "Building & Wards",  "BUILDING",     ORANGE);

        sidebar.add(Box.createVerticalGlue());

        JPanel statusChip = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        statusChip.setOpaque(false);
        statusChip.setMaximumSize(new Dimension(210, 26));
        statusChip.setBorder(new EmptyBorder(0, 22, 6, 0));
        JLabel dot = new JLabel("\u25CF");
        dot.setForeground(EMERALD);
        dot.setFont(FONT_SMALL);
        JLabel statusText = new JLabel("System Online");
        statusText.setForeground(TEXT_MUTED);
        statusText.setFont(FONT_SMALL);
        statusChip.add(dot);
        statusChip.add(statusText);
        sidebar.add(statusChip);

        // ── Exit button ────────────────────────────────────────────────────────
        JButton exitBtn = new GradientButton("Exit Application", DANGER, DANGER_DARK);
        exitBtn.setMaximumSize(new Dimension(200, 42));
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> {
            int c = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to exit?", "Confirm Exit",
                    JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) System.exit(0);
        });
        JPanel exitWrap = new JPanel();
        exitWrap.setOpaque(false);
        exitWrap.setLayout(new BoxLayout(exitWrap, BoxLayout.Y_AXIS));
        exitWrap.setBorder(new EmptyBorder(0, 16, 18, 16));
        exitWrap.add(exitBtn);
        sidebar.add(exitWrap);
        return sidebar;
    }

    private JSeparator sectionDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_SUBTLE);
        sep.setBackground(BORDER_SUBTLE);
        sep.setMaximumSize(new Dimension(232, 1));
        return sep;
    }

    /** Creates and adds a single navigation button to the sidebar. */
    private void addNavItem(JPanel sidebar, String label, String card, Color dotColor) {
        NavButton btn = new NavButton(label, dotColor);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.addActionListener(e -> {
            for (NavButton nb : navButtons) nb.setSelected(nb == btn);
            cardLayout.show(contentPanel, card);
        });
        if (navButtons.isEmpty()) btn.setSelected(true); // Dashboard starts active
        navButtons.add(btn);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 12, 4, 12));
        wrap.setMaximumSize(new Dimension(232, 48));
        wrap.add(btn, BorderLayout.CENTER);
        sidebar.add(wrap);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Dashboard
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildDashboard() {
        JPanel panel = darkPanel(new BorderLayout(0, 22));
        panel.setBorder(new EmptyBorder(28, 32, 28, 32));

        panel.add(heroBanner(), BorderLayout.NORTH);

        JPanel centerWrap = new JPanel(new BorderLayout(0, 18));
        centerWrap.setOpaque(false);

        JLabel gridTitle = new JLabel("Live Overview");
        gridTitle.setFont(FONT_SUBTITLE);
        gridTitle.setForeground(TEXT_LIGHT);
        centerWrap.add(gridTitle, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(2, 3, 18, 18));
        cards.setOpaque(false);

        cards.add(liveStatCard("Doctors",      "doctors",  CYAN));
        cards.add(liveStatCard("Nurses",       "nurses",   EMERALD));
        cards.add(liveStatCard("Patients",     "patients", AMBER));
        cards.add(liveStatCard("Appointments", "appts",    ORCHID));
        cards.add(infoCard("Hospital", "Paradise Hospital\nDhaka, Bangladesh", PRIMARY_LIGHT));
        cards.add(infoCard("System",   "Status: Online\nVersion: 3.0",         ORANGE));

        centerWrap.add(cards, BorderLayout.CENTER);
        panel.add(centerWrap, BorderLayout.CENTER);

        JLabel footer = new JLabel(
            "Paradise Hospital Management System  \u2022  Java Swing  \u2022  v3.0",
            SwingConstants.CENTER);
        footer.setFont(FONT_SMALL);
        footer.setForeground(TEXT_DIM);
        panel.add(footer, BorderLayout.SOUTH);
        return panel;
    }

    /** Large gradient hero card greeting the user at the top of the dashboard. */
    private JPanel heroBanner() {
        JPanel hero = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_DARK, getWidth(), getHeight(), new Color(0x22B8CE));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);

                // decorative translucent circles
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(getWidth() - 160, -60, 220, 220);
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillOval(getWidth() - 260, 60, 140, 140);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        hero.setOpaque(false);
        hero.setPreferredSize(new Dimension(10, 118));
        hero.setBorder(new EmptyBorder(22, 28, 22, 28));

        String greeting = timeGreeting();
        String today = LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));

        JLabel title = new JLabel(greeting + " \u2014 welcome back");
        title.setFont(FONT_HERO);
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Here's what's happening at Paradise Hospital today, " + today + ".");
        subtitle.setFont(FONT_NORMAL);
        subtitle.setForeground(new Color(255, 255, 255, 210));

        JPanel textStack = new JPanel();
        textStack.setOpaque(false);
        textStack.setLayout(new BoxLayout(textStack, BoxLayout.Y_AXIS));
        textStack.add(title);
        textStack.add(Box.createVerticalStrut(8));
        textStack.add(subtitle);

        hero.add(textStack, BorderLayout.WEST);
        return hero;
    }

    private String timeGreeting() {
        int hour = LocalDateTime.now().getHour();
        if (hour < 12) return "Good morning";
        if (hour < 17) return "Good afternoon";
        return "Good evening";
    }

    /**
     * Creates a stat card on the dashboard that auto-refreshes its count
     * every 1.5 seconds using a Swing Timer.
     *
     * @param label   Display label shown above the number
     * @param dataKey One of: "doctors", "nurses", "patients", "appts"
     * @param accent  Colour used for the large count number and accent bar
     */
    private JPanel liveStatCard(String label, String dataKey, Color accent) {
        JPanel card = roundedCard();
        card.setLayout(new BorderLayout(6, 6));
        card.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(monogramBadge(label.substring(0, 1), accent, accent.darker(), 34), BorderLayout.WEST);
        JLabel titleLbl = new JLabel(label);
        titleLbl.setFont(FONT_SUBTITLE);
        titleLbl.setForeground(TEXT_LIGHT);
        titleLbl.setBorder(new EmptyBorder(0, 10, 0, 0));
        top.add(titleLbl, BorderLayout.CENTER);

        JLabel countLbl = new JLabel("0");
        countLbl.setFont(FONT_NUMBER);
        countLbl.setForeground(accent);
        countLbl.setBorder(new EmptyBorder(6, 2, 4, 0));

        JPanel accentBar = accentUnderline(accent);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(countLbl, BorderLayout.CENTER);
        bottom.add(accentBar, BorderLayout.SOUTH);

        card.add(top, BorderLayout.NORTH);
        card.add(bottom, BorderLayout.CENTER);

        // Refresh the count on a background timer
        new Timer(1500, e -> {
            int count;
            switch (dataKey) {
                case "doctors":  count = service.getAllDoctors().size();  break;
                case "nurses":   count = service.getAllNurses().size();   break;
                case "patients": count = service.getAllPatients().size(); break;
                case "appts": count = (int) service.getAppointmentSummary().stream().filter(s -> !s.startsWith("No")).count();break;
                default:count = 0; break;
            }
            countLbl.setText(String.valueOf(count));
        }).start();

        return card;
    }

    /** Non-live informational card for static content on the dashboard. */
    private JPanel infoCard(String title, String body, Color accent) {
        JPanel card = roundedCard();
        card.setLayout(new BorderLayout(8, 8));
        card.setBorder(new EmptyBorder(20, 22, 16, 22));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(monogramBadge(title.substring(0, 1), accent, accent.darker(), 34), BorderLayout.WEST);
        JLabel lbl = new JLabel(title);
        lbl.setFont(FONT_SUBTITLE);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
        top.add(lbl, BorderLayout.CENTER);

        JTextArea area = new JTextArea(body);
        area.setFont(FONT_NORMAL);
        area.setForeground(TEXT_MUTED);
        area.setOpaque(false);
        area.setEditable(false);
        area.setBorder(new EmptyBorder(4, 2, 0, 0));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(area, BorderLayout.CENTER);
        bottom.add(accentUnderline(accent), BorderLayout.SOUTH);

        card.add(top,    BorderLayout.NORTH);
        card.add(bottom, BorderLayout.CENTER);
        return card;
    }

    /** Thin gradient underline used to accent stat/info cards. */
    private JPanel accentUnderline(Color accent) {
        JPanel bar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), 0, new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setPreferredSize(new Dimension(10, 4));
        bar.setMaximumSize(new Dimension(4000, 4));
        return bar;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Doctor Panel
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildDoctorPanel() {
        JPanel panel = darkPanel(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));
        panel.add(sectionTitle("Doctor Management", "Add, review and manage doctors on staff", CYAN), BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Phone", "Address", "Max Capacity", "Assigned Patients"};
        doctorTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(doctorTableModel);
        panel.add(tableCard(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        JButton addBtn     = new GradientButton("+ Add Doctor",   PRIMARY_LIGHT, PRIMARY_DARK);
        JButton removeBtn  = new GradientButton("Remove",         DANGER, DANGER_DARK);
        JButton viewBtn    = new GradientButton("View Details",   BORDER_SUBTLE, CARD_BG);
        JButton refreshBtn = new GradientButton("\u21BB Refresh", BORDER_SUBTLE, CARD_BG);

        addBtn.addActionListener(e -> showAddDoctorDialog());
        removeBtn.addActionListener(e -> removeSelected(table, doctorTableModel, "doctor"));
        viewBtn.addActionListener(e -> viewDoctorDetails(table));
        refreshBtn.addActionListener(e -> refreshDoctorTable());

        buttons.add(addBtn);
        buttons.add(removeBtn);
        buttons.add(viewBtn);
        buttons.add(refreshBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddDoctorDialog() {
        JTextField[] f = newFields(7);
        String[] labels = {
            "Doctor ID", "Full Name", "Phone",
            "ZIP Code", "City", "Street",
            "Max Patients (1-100)"
        };
        if (showFormDialog("Add New Doctor", f, labels) != JOptionPane.OK_OPTION) return;

        String id     = f[0].getText().trim();
        String name   = f[1].getText().trim();
        String phone  = f[2].getText().trim();
        String zip    = f[3].getText().trim();
        String city   = f[4].getText().trim();
        String street = f[5].getText().trim();
        String capStr = f[6].getText().trim();

        if (!Validator.allNonEmpty(id, name, phone, zip, city, street, capStr)) {
            showError("All fields are required."); return;
        }
        if (!Validator.isValidId(id)) {
            showError("ID must be 2\u201320 alphanumeric characters."); return;
        }
        if (!Validator.isValidPhone(phone)) {
            showError("Invalid phone number."); return;
        }
        int cap = Validator.parsePositiveInt(capStr, 1, 100);
        if (cap == -1) {
            showError("Capacity must be a number between 1 and 100."); return;
        }

        if (service.addDoctor(id, name, phone, new Address(zip, city, street), cap)) {
            showSuccess("Doctor \"" + name + "\" added successfully!");
            refreshDoctorTable();
        } else {
            showError("A doctor with ID \"" + id + "\" already exists.");
        }
    }

    private void refreshDoctorTable() {
        doctorTableModel.setRowCount(0);
        for (Doctor d : service.getAllDoctors()) {
            doctorTableModel.addRow(new Object[]{
                d.getId(), d.getName(), d.getPhone(),
                d.getAddress(), d.getMaxPatientCapacity(),
                d.getCurrentPatientCount()
            });
        }
    }

    private void viewDoctorDetails(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Please select a doctor row first."); return; }

        String id = (String) doctorTableModel.getValueAt(row, 0);
        Doctor d  = service.findDoctor(id);
        if (d == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append(d).append("\n\nAssigned Patients:\n");
        if (d.getAssignedPatientIds().isEmpty()) {
            sb.append("  (none assigned)");
        } else {
            for (String pid : d.getAssignedPatientIds()) {
                Patient p = service.findPatient(pid);
                String pName = (p != null) ? p.getName() : "Unknown";
                sb.append("  \u2022 ").append(pName).append("  (ID: ").append(pid).append(")\n");
            }
        }
        showInfo("Doctor Details \u2014 " + d.getName(), sb.toString());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Nurse Panel
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildNursePanel() {
        JPanel panel = darkPanel(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));
        panel.add(sectionTitle("Nurse Management", "Add, review and manage nursing staff", EMERALD), BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Phone", "Address", "Ward Capacity", "Assigned Wards"};
        nurseTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(nurseTableModel);
        panel.add(tableCard(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        JButton addBtn     = new GradientButton("+ Add Nurse",    EMERALD, new Color(0x1F9C71));
        JButton removeBtn  = new GradientButton("Remove",         DANGER, DANGER_DARK);
        JButton refreshBtn = new GradientButton("\u21BB Refresh", BORDER_SUBTLE, CARD_BG);

        addBtn.addActionListener(e -> showAddNurseDialog());
        removeBtn.addActionListener(e -> removeSelected(table, nurseTableModel, "nurse"));
        refreshBtn.addActionListener(e -> refreshNurseTable());

        buttons.add(addBtn);
        buttons.add(removeBtn);
        buttons.add(refreshBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddNurseDialog() {
        JTextField[] f = newFields(7);
        String[] labels = {
            "Nurse ID", "Full Name", "Phone",
            "ZIP Code", "City", "Street",
            "Max Wards (1-20)"
        };
        if (showFormDialog("Add New Nurse", f, labels) != JOptionPane.OK_OPTION) return;

        String id     = f[0].getText().trim();
        String name   = f[1].getText().trim();
        String phone  = f[2].getText().trim();
        String zip    = f[3].getText().trim();
        String city   = f[4].getText().trim();
        String street = f[5].getText().trim();
        String capStr = f[6].getText().trim();

        if (!Validator.allNonEmpty(id, name, phone, zip, city, street, capStr)) {
            showError("All fields are required."); return;
        }
        if (!Validator.isValidId(id)) {
            showError("ID must be 2\u201320 alphanumeric characters."); return;
        }
        if (!Validator.isValidPhone(phone)) {
            showError("Invalid phone number."); return;
        }
        int cap = Validator.parsePositiveInt(capStr, 1, 20);
        if (cap == -1) {
            showError("Ward capacity must be a number between 1 and 20."); return;
        }

        if (service.addNurse(id, name, phone, new Address(zip, city, street), cap)) {
            showSuccess("Nurse \"" + name + "\" added successfully!");
            refreshNurseTable();
        } else {
            showError("A nurse with ID \"" + id + "\" already exists.");
        }
    }

    private void refreshNurseTable() {
        nurseTableModel.setRowCount(0);
        for (Nurse n : service.getAllNurses()) {
            nurseTableModel.addRow(new Object[]{
                n.getId(), n.getName(), n.getPhone(),
                n.getAddress(), n.getMaxWardCapacity(),
                n.getCurrentWardCount()
            });
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Patient Panel
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildPatientPanel() {
        JPanel panel = darkPanel(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));
        panel.add(sectionTitle("Patient Management", "Register and track patient records", AMBER), BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Phone", "Address", "Assigned Ward"};
        patientTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(patientTableModel);
        panel.add(tableCard(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        JButton addBtn     = new GradientButton("+ Add Patient",  AMBER, new Color(0xC2891A));
        JButton removeBtn  = new GradientButton("Remove",         DANGER, DANGER_DARK);
        JButton refreshBtn = new GradientButton("\u21BB Refresh", BORDER_SUBTLE, CARD_BG);

        addBtn.addActionListener(e -> showAddPatientDialog());
        removeBtn.addActionListener(e -> removeSelected(table, patientTableModel, "patient"));
        refreshBtn.addActionListener(e -> refreshPatientTable());

        buttons.add(addBtn);
        buttons.add(removeBtn);
        buttons.add(refreshBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void showAddPatientDialog() {
        JTextField[] f = newFields(7);
        String[] labels = {
            "Patient ID", "Full Name", "Phone",
            "ZIP Code", "City", "Street",
            "Ward Number"
        };
        if (showFormDialog("Add New Patient", f, labels) != JOptionPane.OK_OPTION) return;

        String id     = f[0].getText().trim();
        String name   = f[1].getText().trim();
        String phone  = f[2].getText().trim();
        String zip    = f[3].getText().trim();
        String city   = f[4].getText().trim();
        String street = f[5].getText().trim();
        String ward   = f[6].getText().trim();

        if (!Validator.allNonEmpty(id, name, phone, zip, city, street, ward)) {
            showError("All fields are required."); return;
        }
        if (!Validator.isValidId(id)) {
            showError("ID must be 2\u201320 alphanumeric characters."); return;
        }
        if (!Validator.isValidPhone(phone)) {
            showError("Invalid phone number."); return;
        }

        if (service.addPatient(id, name, phone, new Address(zip, city, street), ward)) {
            showSuccess("Patient \"" + name + "\" added successfully!");
            refreshPatientTable();
        } else {
            showError("A patient with ID \"" + id + "\" already exists.");
        }
    }

    private void refreshPatientTable() {
        patientTableModel.setRowCount(0);
        for (Patient p : service.getAllPatients()) {
            patientTableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getPhone(),
                p.getAddress(), p.getAssignedWardNumber()
            });
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Appointment Panel
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildAppointmentPanel() {
        JPanel panel = darkPanel(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));
        panel.add(sectionTitle("Appointment Management", "Book and manage doctor-patient appointments", ORCHID), BorderLayout.NORTH);

        String[] cols = {"Doctor ID", "Doctor Name", "Patient ID", "Patient Name"};
        apptTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(apptTableModel);
        panel.add(tableCard(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        JButton bookBtn    = new GradientButton("+ Book Appointment", ORCHID, new Color(0x9146D6));
        JButton cancelBtn  = new GradientButton("Cancel",             DANGER, DANGER_DARK);
        JButton refreshBtn = new GradientButton("\u21BB Refresh",     BORDER_SUBTLE, CARD_BG);

        bookBtn.addActionListener(e -> showBookAppointmentDialog());
        cancelBtn.addActionListener(e -> cancelSelectedAppointment(table));
        refreshBtn.addActionListener(e -> refreshAppointmentTable());

        buttons.add(bookBtn);
        buttons.add(cancelBtn);
        buttons.add(refreshBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void showBookAppointmentDialog() {
        JTextField doctorField  = styledField();
        JTextField patientField = styledField();

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(12, 16, 12, 16));
        form.add(formLabel("Doctor ID:"));  form.add(doctorField);
        form.add(formLabel("Patient ID:")); form.add(patientField);

        int opt = JOptionPane.showConfirmDialog(this, form, "Book Appointment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        String dId = doctorField.getText().trim();
        String pId = patientField.getText().trim();

        if (!Validator.allNonEmpty(dId, pId)) {
            showError("Both Doctor ID and Patient ID are required."); return;
        }
        if (service.findDoctor(dId) == null) {
            showError("Doctor ID \"" + dId + "\" not found."); return;
        }
        if (service.findPatient(pId) == null) {
            showError("Patient ID \"" + pId + "\" not found."); return;
        }

        if (service.bookAppointment(dId, pId)) {
            showSuccess("Appointment booked successfully!");
            refreshAppointmentTable();
        } else {
            showError("Could not book appointment.\nThe doctor may be at full capacity or\nthis patient is already assigned to them.");
        }
    }

    private void cancelSelectedAppointment(JTable table) {
        int row = table.getSelectedRow();
        if (row == -1) { showError("Please select an appointment row to cancel."); return; }

        String dId    = (String) apptTableModel.getValueAt(row, 0);
        String dName  = (String) apptTableModel.getValueAt(row, 1);
        String pId    = (String) apptTableModel.getValueAt(row, 2);
        String pName  = (String) apptTableModel.getValueAt(row, 3);

        int c = JOptionPane.showConfirmDialog(this,
                "Cancel appointment:\n  Dr. " + dName + "  \u2192  " + pName + "?",
                "Confirm Cancel", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            service.cancelAppointment(dId, pId);
            showSuccess("Appointment cancelled.");
            refreshAppointmentTable();
        }
    }

    private void refreshAppointmentTable() {
        apptTableModel.setRowCount(0);
        for (Doctor d : service.getAllDoctors()) {
            for (String pid : d.getAssignedPatientIds()) {
                Patient p = service.findPatient(pid);
                if (p != null) {
                    apptTableModel.addRow(new Object[]{
                        d.getId(), d.getName(), p.getId(), p.getName()
                    });
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Building Panel
    // ══════════════════════════════════════════════════════════════════════════

    private JPanel buildBuildingPanel() {
        JPanel panel = darkPanel(new BorderLayout(0, 16));
        panel.setBorder(new EmptyBorder(26, 30, 26, 30));
        panel.add(sectionTitle("Building & Ward Management", "Configure rooms, wards and assignments", ORANGE), BorderLayout.NORTH);

        buildingTextArea = new JTextArea(
            "Building not initialised.\nClick \"Setup Building\" to create rooms and wards.");
        buildingTextArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        buildingTextArea.setBackground(CARD_BG);
        buildingTextArea.setForeground(TEXT_LIGHT);
        buildingTextArea.setEditable(false);
        buildingTextArea.setLineWrap(false);
        buildingTextArea.setBorder(new EmptyBorder(16, 18, 16, 18));

        JScrollPane scroll = new JScrollPane(buildingTextArea);
        styleScrollPane(scroll);
        JPanel card = roundedCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(4, 4, 4, 4));
        card.add(scroll, BorderLayout.CENTER);
        panel.add(card, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttons.setOpaque(false);

        JButton setupBtn    = new GradientButton("Setup Building", PRIMARY_LIGHT, PRIMARY_DARK);
        JButton assignDrBtn = new GradientButton("Assign Doctor",  CYAN, new Color(0x1A9CB3));
        JButton assignNrBtn = new GradientButton("Assign Nurse",   AMBER, new Color(0xC2891A));
        JButton assignPtBtn = new GradientButton("Assign Patient", ORCHID, new Color(0x9146D6));
        JButton refreshBtn  = new GradientButton("\u21BB Refresh View", BORDER_SUBTLE, CARD_BG);

        setupBtn.addActionListener(e -> showSetupBuildingDialog());
        assignDrBtn.addActionListener(e -> showAssignToWardDialog("doctor"));
        assignNrBtn.addActionListener(e -> showAssignToWardDialog("nurse"));
        assignPtBtn.addActionListener(e -> showAssignToWardDialog("patient"));
        refreshBtn.addActionListener(e -> refreshBuildingView());

        buttons.add(setupBtn);
        buttons.add(assignDrBtn);
        buttons.add(assignNrBtn);
        buttons.add(assignPtBtn);
        buttons.add(refreshBtn);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private void showSetupBuildingDialog() {
        JTextField nameField  = styledField(); nameField.setText("Paradise Hospital");
        JTextField roomsField = styledField(); roomsField.setText("3");
        JTextField wardsField = styledField(); wardsField.setText("4");

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(12, 16, 12, 16));
        form.add(formLabel("Building Name:"));   form.add(nameField);
        form.add(formLabel("Number of Rooms:")); form.add(roomsField);
        form.add(formLabel("Wards per Room:"));  form.add(wardsField);

        int opt = JOptionPane.showConfirmDialog(this, form,
                "Setup Building", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        String name  = nameField.getText().trim();
        int rooms    = Validator.parsePositiveInt(roomsField.getText(), 1, 20);
        int wards    = Validator.parsePositiveInt(wardsField.getText(), 1, 20);

        if (!Validator.isNonEmpty(name))  { showError("Building name is required."); return; }
        if (rooms == -1) { showError("Number of rooms must be between 1 and 20."); return; }
        if (wards == -1) { showError("Wards per room must be between 1 and 20."); return; }

        service.initBuilding(name, rooms, wards);
        showSuccess("Building \"" + name + "\" created:\n" + rooms + " rooms  \u00d7  " + wards + " wards each.");
        refreshBuildingView();
    }

    private void showAssignToWardDialog(String role) {
        JTextField roomField = styledField();
        JTextField wardField = styledField();
        JTextField idField   = styledField();

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(12, 16, 12, 16));
        form.add(formLabel("Room Number:"));              form.add(roomField);
        form.add(formLabel("Ward Number:"));              form.add(wardField);
        form.add(formLabel(capitalize(role) + " ID:"));  form.add(idField);

        String dialogTitle = "Assign " + capitalize(role) + " to Ward";
        int opt = JOptionPane.showConfirmDialog(this, form, dialogTitle,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opt != JOptionPane.OK_OPTION) return;

        int    room = Validator.parsePositiveInt(roomField.getText(), 1, 50);
        int    ward = Validator.parsePositiveInt(wardField.getText(), 1, 50);
        String id   = idField.getText().trim();

        if (room == -1 || ward == -1) {
            showError("Room and ward numbers must be positive integers."); return;
        }
        if (!Validator.isNonEmpty(id)) {
            showError(capitalize(role) + " ID is required."); return;
        }

        boolean success;
        if      ("doctor".equals(role))  success = service.assignDoctorToWard(room, ward, id);
        else if ("nurse".equals(role))   success = service.assignNurseToWard(room, ward, id);
        else                             success = service.assignPatientToWard(room, ward, id);

        if (success) {
            showSuccess(capitalize(role) + " (ID: " + id + ") assigned to Room " + room + ", Ward " + ward + ".");
            refreshBuildingView();
        } else {
            showError("Assignment failed.\n\nMake sure:\n"
                    + "  \u2022 The building has been set up\n"
                    + "  \u2022 Room " + room + " and Ward " + ward + " exist\n"
                    + "  \u2022 " + capitalize(role) + " ID \"" + id + "\" is registered");
        }
    }

    private void refreshBuildingView() {
        buildingTextArea.setText(service.getBuildingSummary());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Shared Remove Helper
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Handles remove-button clicks for all three entity types.
     * Reads the ID from column 0 of the selected row, confirms with the user,
     * calls the appropriate service method, then refreshes the table.
     */
    private void removeSelected(JTable table, DefaultTableModel model, String entityType) {
        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Please select a row to remove."); return;
        }
        String id = (String) model.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove " + capitalize(entityType) + " with ID: \"" + id + "\"?\nThis action cannot be undone.",
                "Confirm Remove", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean removed;
        if      ("doctor".equals(entityType))  removed = service.removeDoctor(id);
        else if ("nurse".equals(entityType))   removed = service.removeNurse(id);
        else                                   removed = service.removePatient(id);

        if (removed) {
            showSuccess(capitalize(entityType) + " removed successfully.");
            if      ("doctor".equals(entityType))  refreshDoctorTable();
            else if ("nurse".equals(entityType))   refreshNurseTable();
            else                                   refreshPatientTable();
        } else {
            showError("Could not remove — " + entityType + " with ID \"" + id + "\" not found.");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Form Dialog Factory
    // ══════════════════════════════════════════════════════════════════════════

    /** Allocates an array of pre-styled text fields. */
    private JTextField[] newFields(int count) {
        JTextField[] fields = new JTextField[count];
        for (int i = 0; i < count; i++) fields[i] = styledField();
        return fields;
    }

    /**
     * Builds a GridLayout form from parallel arrays of fields and labels,
     * shows it in a JOptionPane, and returns the user's choice constant.
     */
    private int showFormDialog(String title, JTextField[] fields, String[] labels) {
        JPanel form = new JPanel(new GridLayout(fields.length, 2, 10, 8));
        form.setBackground(CARD_BG);
        form.setBorder(new EmptyBorder(12, 16, 12, 16));
        for (int i = 0; i < fields.length; i++) {
            form.add(formLabel(labels[i] + ":"));
            form.add(fields[i]);
        }
        return JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  UI Component Factories
    // ══════════════════════════════════════════════════════════════════════════

    /** Creates a JPanel with a subtle diagonal gradient background. */
    private JPanel darkPanel(LayoutManager layout) {
        JPanel p = new JPanel(layout) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, BG_DEEP, getWidth(), getHeight(), BG_DEEP_2);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        return p;
    }

    /**
     * Creates a card panel with rounded corners and a soft drop shadow.
     * Uses an anonymous subclass that overrides paintComponent so we can
     * fill a RoundRect (plus shadow layers) before Swing paints children.
     */
    private JPanel roundedCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                    RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                // soft shadow (a few translucent layers offset downward)
                for (int i = 3; i >= 1; i--) {
                    g2.setColor(new Color(0, 0, 0, 18));
                    g2.fillRoundRect(i, i + 2, w - 2 * i, h - 2 * i, 18, 18);
                }
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0, 0, w - 2, h - 4, 18, 18);
                g2.setColor(BORDER_SUBTLE);
                g2.drawRoundRect(0, 0, w - 3, h - 5, 18, 18);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        return card;
    }

    /** Wraps a JTable + scroll pane inside a rounded card for the panel body. */
    private JPanel tableCard(JTable table) {
        JScrollPane scroll = new JScrollPane(table);
        styleScrollPane(scroll);
        JPanel card = roundedCard();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(6, 6, 6, 6));
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /** Section heading block: bold title + muted subtitle + gradient underline. */
    private JPanel sectionTitle(String text, String subtitle, Color accent) {
        JPanel wrap = new JPanel();
        wrap.setOpaque(false);
        wrap.setLayout(new BoxLayout(wrap, BoxLayout.Y_AXIS));
        wrap.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_TITLE);
        lbl.setForeground(TEXT_LIGHT);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel(subtitle);
        sub.setFont(FONT_NORMAL);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(2, 0, 10, 0));

        JPanel bar = accentUnderline(accent);
        bar.setPreferredSize(new Dimension(60, 4));
        bar.setMaximumSize(new Dimension(60, 4));
        bar.setAlignmentX(Component.LEFT_ALIGNMENT);

        wrap.add(lbl);
        wrap.add(sub);
        wrap.add(bar);
        return wrap;
    }

    /** Right-side label inside a form grid. */
    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_NORMAL);
        lbl.setForeground(TEXT_LIGHT);
        return lbl;
    }

    /** Pre-styled dark text field that matches the app theme. */
    private JTextField styledField() {
        JTextField f = new JTextField(18);
        f.setBackground(FIELD_BG);
        f.setForeground(TEXT_LIGHT);
        f.setCaretColor(TEXT_LIGHT);
        f.setFont(FONT_NORMAL);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_LIGHT, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    /** A small circular/rounded "monogram" badge used for logos and icons. */
    private JComponent monogramBadge(String letters, Color from, Color to, int size) {
        JComponent badge = new JComponent() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, from, size, size, to);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, size, size, size / 3, size / 3);
                g2.setFont(new Font("Segoe UI", Font.BOLD, Math.max(11, size / 3)));
                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                int tx = (size - fm.stringWidth(letters)) / 2;
                int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(letters, tx, ty);
                g2.dispose();
            }
        };
        badge.setPreferredSize(new Dimension(size, size));
        badge.setMaximumSize(new Dimension(size, size));
        badge.setOpaque(false);
        return badge;
    }

    /**
     * Applies the dark theme to a JTable including:
     *  - Gradient header row
     *  - Alternating row colours
     *  - Highlighted selection
     *  - Cell padding via custom renderer
     */
    private JTable styledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_NORMAL);
        table.setForeground(TEXT_LIGHT);
        table.setBackground(CARD_BG);
        table.setGridColor(BORDER_SUBTLE);
        table.setRowHeight(32);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setAutoCreateRowSorter(true);       // click header to sort

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBTITLE);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(0, 38));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                c.setOpaque(false);
                c.setForeground(Color.WHITE);
                c.setBorder(new EmptyBorder(0, 10, 0, 10));
                JPanel wrapper = new JPanel(new BorderLayout()) {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        GradientPaint gp = new GradientPaint(0, 0, PRIMARY_DARK, getWidth(), 0, new Color(0x1AA0C9));
                        g2.setPaint(gp);
                        g2.fillRect(0, 0, getWidth(), getHeight());
                        g2.dispose();
                    }
                };
                wrapper.add(c, BorderLayout.CENTER);
                return wrapper;
            }
        });

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    setBackground(row % 2 == 0 ? CARD_BG : CARD_BG_HOVER);
                    setForeground(TEXT_LIGHT);
                }
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        return table;
    }

    /** Styles a JScrollPane to match the dark theme. */
    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getViewport().setBackground(CARD_BG);
        sp.getVerticalScrollBar().setBackground(CARD_BG);
        sp.getHorizontalScrollBar().setBackground(CARD_BG);
    }

    // ── Dialog shortcuts ───────────────────────────────────────────────────────

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String title, String msg) {
        JTextArea area = new JTextArea(msg);
        area.setEditable(false);
        area.setFont(FONT_NORMAL);
        area.setBackground(CARD_BG);
        area.setForeground(TEXT_LIGHT);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(520, 280));
        styleScrollPane(sp);
        JOptionPane.showMessageDialog(this, sp, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /** Uppercases the first character of a string. */
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Custom Components
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * A JButton subclass that paints a smooth rounded gradient background,
     * a subtle drop shadow, and a lighten-on-hover / darken-on-press effect.
     * Used for every action button in the app instead of the default L&F button.
     */
    private static class GradientButton extends JButton {
        private final Color colorA;
        private final Color colorB;
        private boolean hover  = false;
        private boolean pressed = false;

        GradientButton(String text, Color colorA, Color colorB) {
            super(text);
            this.colorA = colorA;
            this.colorB = colorB;
            setFont(FONT_BUTTON);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10, 20, 10, 20));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
                @Override public void mousePressed(MouseEvent e) { pressed = true; repaint(); }
                @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            Color a = pressed ? colorA.darker() : (hover ? brighten(colorA) : colorA);
            Color b = pressed ? colorB.darker() : (hover ? brighten(colorB) : colorB);

            // shadow
            g2.setColor(new Color(0, 0, 0, hover ? 70 : 45));
            g2.fillRoundRect(0, 3, w, h - 3, 12, 12);

            GradientPaint gp = new GradientPaint(0, 0, a, w, h, b);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h - 2, 12, 12);
            g2.dispose();
            super.paintComponent(g);
        }

        private Color brighten(Color c) {
            int r = Math.min(255, c.getRed()   + 18);
            int gg = Math.min(255, c.getGreen() + 18);
            int b = Math.min(255, c.getBlue()  + 18);
            return new Color(r, gg, b);
        }
    }

    /**
     * A sidebar navigation button: transparent by default, shows a soft
     * highlight on hover, and a solid gradient pill with a coloured dot
     * indicator when selected/active.
     */
    private static class NavButton extends JButton {
        private final Color dotColor;
        private boolean selected = false;
        private boolean hover = false;

        NavButton(String text, Color dotColor) {
            super("   " + text);
            this.dotColor = dotColor;
            setFont(FONT_NORMAL);
            setForeground(TEXT_LIGHT);
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(11, 14, 11, 10));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hover = true;  repaint(); }
                @Override public void mouseExited(MouseEvent e)  { hover = false; repaint(); }
            });
        }

        @Override
        public void setSelected(boolean sel) {
            this.selected = sel;
            setForeground(sel ? Color.WHITE : TEXT_LIGHT);
            setFont(sel ? FONT_SUBTITLE : FONT_NORMAL);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();

            if (selected) {
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY, w, 0, PRIMARY_DARK);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            } else if (hover) {
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillRoundRect(0, 0, w, h, 12, 12);
            }

            // dot indicator
            int dotSize = 8;
            int dy = (h - dotSize) / 2;
            g2.setColor(selected ? Color.WHITE : dotColor);
            g2.fillOval(14, dy, dotSize, dotSize);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
