
import java.awt.*;                     // GUI Components (e.g., colors, fonts, layout managers)
import java.io.*;          
import java.net.*;                    // Network Communication
import java.nio.file.Files;          // Supports File Handling Operations 
import java.text.SimpleDateFormat;  // Date/Time processing for scheduled mail & sending history features
import java.util.*;                // Utilities of Date, Timer, List, ArrayList .,etc
import java.util.List;
import javax.swing.*;             // GUI Framework 
import javax.swing.table.DefaultTableModel; 





public class SMTPClientJavaGUI extends JFrame {
    private JTextField sender_Field, recipient_Field, subject_Field, username_Field;
    private JPasswordField password_Field;
    private JTextArea body_Area;
    private JButton send_Button, attach_Button;
    private JTable sending_history_Table;
    private DefaultTableModel sending_history_Model;


    // FEATURE.1 - Support of multiple file attachments (of any type)
    private List<File> Attachments = new ArrayList<>();




    // Email history is saved in an auto-generated CSV file ("sent-email.csv") permanently. 
    // (for the Feature.4 - Record of Sending History)
    private static final String CSV_FILE = "sent-email.csv";
    private JSpinner scheduled_Spinner;

    



    
    // A constructor for the GUI components
    // Input boxes for sender, recipients, subject, username, password, & message body.
    // A Spinner is used for selecting the date and time for scheduled email.
    // BorderLayout, GridLayout, JScrollPane, & JSplitPane for the interface organization.
    
    public SMTPClientJavaGUI() {
        setTitle("SMTP Client Application with AUTH LOGIN");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel main_panel = new JPanel(new BorderLayout());

        JPanel form_Panel = new JPanel(new GridLayout(9, 2));

        sender_Field = new JTextField();
        recipient_Field = new JTextField();
        subject_Field = new JTextField();
        username_Field = new JTextField();
        password_Field = new JPasswordField();
        body_Area = new JTextArea(10, 40);

        attach_Button = new JButton("Attach Files");
        attach_Button.addActionListener(e -> Attachments_Selection());

        send_Button = new JButton("Send Email");
        send_Button.addActionListener(e -> Scheduled_Email());

        scheduled_Spinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editor = new JSpinner.DateEditor(scheduled_Spinner, "yyyy-MM-dd HH:mm:ss");
        scheduled_Spinner.setEditor(editor);
        scheduled_Spinner.setValue(new Date());

        form_Panel.add(new JLabel("From:"));
        form_Panel.add(sender_Field);
        form_Panel.add(new JLabel("To (comma-separated):"));
        form_Panel.add(recipient_Field);
        form_Panel.add(new JLabel("Subject:"));
        form_Panel.add(subject_Field);
        form_Panel.add(new JLabel("Username:"));
        form_Panel.add(username_Field);
        form_Panel.add(new JLabel("Password:"));
        form_Panel.add(password_Field);
        form_Panel.add(new JLabel("Attachments:"));
        form_Panel.add(attach_Button);
        form_Panel.add(new JLabel("Schedule Send Time:"));
        form_Panel.add(scheduled_Spinner);

        main_panel.add(form_Panel, BorderLayout.NORTH);
        main_panel.add(new JScrollPane(body_Area), BorderLayout.CENTER);
        main_panel.add(send_Button, BorderLayout.SOUTH);

        String[] columns = { "From", "To", "Subject", "Time", "Status" };
        sending_history_Model = new DefaultTableModel(columns, 0);
        sending_history_Table = new JTable(sending_history_Model);

        JPanel history_Panel = new JPanel(new BorderLayout());
        history_Panel.setBorder(BorderFactory.createTitledBorder("Email History"));
        history_Panel.add(new JScrollPane(sending_history_Table), BorderLayout.CENTER);

        JSplitPane split_Pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, main_panel, history_Panel);
        split_Pane.setDividerLocation(400);
        add(split_Pane);

        History_Load();
    }
    



    // Funtion for Choosing of attached files (of any type)
    private void Attachments_Selection() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            Attachments.clear();
            Attachments.addAll(Arrays.asList(fileChooser.getSelectedFiles()));
            attach_Button.setText("Attached: " + Attachments.size() + " file(s)");
        }
    }


    // The sent email must follow the standard format: "name@domain."
    private boolean isValidEmail(String email) {
        return email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }





    // FEATURE.2 - SCHEDULED EMAIL SENDING
    // The user can select an optional future date & time      
    /* 
    The actual sending will be later than the scheduled time 1-2 s,
    this is because of the system thread scheduling & the time for SMTP connection establishment. 
    */     
    private void Scheduled_Email() {
        Date selectedDate = (Date) scheduled_Spinner.getValue();
        long delay = selectedDate.getTime() - System.currentTimeMillis();
        if (delay <= 0) {
            sendEmail();
        } else {
            JOptionPane.showMessageDialog(this, "Email scheduled for: " + selectedDate);
            java.util.Timer timer = new java.util.Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    SwingUtilities.invokeLater(() -> sendEmail());
                }
            }, delay);
            String from = sender_Field.getText().trim();
            String toAll = recipient_Field.getText().trim();
            String subject = subject_Field.getText().trim();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(selectedDate);
            sending_history_Model.addRow(new Object[] { from, toAll, subject, timestamp, "Scheduled" });
            saveEmailToCSV(from, toAll, subject, timestamp, "Scheduled");
        }
    }

    private void sendEmail() {
        String from = sender_Field.getText().trim();
        String toAll = recipient_Field.getText().trim();
        String subject = subject_Field.getText().trim();
        String username = username_Field.getText().trim();
        String password = new String(password_Field.getPassword());
        String body = body_Area.getText();

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (Socket socket = new Socket("localhost", 1025);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            readResponse(in);
            sendCommand(out, in, "HELO localhost");






        //  --- SMTP AUTHENTICATION PROCESS ---
        //   The client starts the SMTP authentication (AUTH LOGIN).
        //   The server then respond with prompts for username and password.
        //   The client responds to each prompt with Base64-encoded credentials.

            sendCommand(out, in, "AUTH LOGIN");
            sendCommand(out, in, Base64.getEncoder().encodeToString(username.getBytes()));
            sendCommand(out, in, Base64.getEncoder().encodeToString(password.getBytes()));


 

            
        // --- SMTP EMAIL SENDING PROCESS ---

            // The sender's email address            
            sendCommand(out, in, "MAIL FROM:<" + from + ">");

            // FEATURE.3 - Support of Multiple Recipients 
            // The user can input several email addresses separated by commas.
            String[] recipients = toAll.split(",");
            for (String recipient : recipients) {
                recipient = recipient.trim();
                if (!isValidEmail(recipient)) {
                    JOptionPane.showMessageDialog(this, "Invalid email address: " + recipient);
                    return;
                }

            // The recipients' email addresses
            sendCommand(out, in, "RCPT TO:<" + recipient + ">");
            }

            // The Email content
            sendCommand(out, in, "DATA");


            // Email headers & body 
            // MIME multipart format for included attachments encoded in base64, or send plain text if no attachments.
            // MIME (Multipurpose Internet Mail Extensions) enables sending emails with attachments and different content types.
            String boundary = "----=_Boundary_" + System.currentTimeMillis();
            out.write("Subject: " + subject + "\r\n");
            out.write("From: " + from + "\r\n");
            out.write("To: " + toAll + "\r\n");
            out.write("MIME-Version: 1.0\r\n");

            if (!Attachments.isEmpty()) {
                out.write("Content-Type: multipart/mixed; boundary=\"" + boundary + "\"\r\n\r\n");
                out.write("--" + boundary + "\r\n");
                out.write("Content-Type: text/plain; charset=utf-8\r\n");
                out.write("Content-Transfer-Encoding: 7bit\r\n\r\n");
                out.write(body + "\r\n");

                for (File file : Attachments) {
                    out.write("--" + boundary + "\r\n");
                    out.write("Content-Type: application/octet-stream; name=\"" + file.getName() + "\"\r\n");
                    out.write("Content-Transfer-Encoding: base64\r\n");
                    out.write("Content-Disposition: attachment; filename=\"" + file.getName() + "\"\r\n\r\n");
                    byte[] fileBytes = Files.readAllBytes(file.toPath());
                    String encoded = Base64.getMimeEncoder().encodeToString(fileBytes);
                    out.write(encoded + "\r\n");
                }
                out.write("--" + boundary + "--\r\n");
            } else {
                out.write("Content-Type: text/plain; charset=utf-8\r\n\r\n");
                out.write(body + "\r\n");
            }

            out.write(".\r\n");
            out.flush();
            readResponse(in);



        // --- SMTP QUIT COMMAND ---
        // The client sends QUIT command to end the session.
        // The server then responds and closes the connection simultaneously.
            sendCommand(out, in, "QUIT");
            JOptionPane.showMessageDialog(this, "Email sent successfully.");
            sending_history_Model.addRow(new Object[] { from, toAll, subject, timestamp, "Sent" });
            saveEmailToCSV(from, toAll, subject, timestamp, "Sent");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending email: " + ex.getMessage());
            sending_history_Model.addRow(new Object[] { from, toAll, subject, timestamp, "Failed" });
            saveEmailToCSV(from, toAll, subject, timestamp, "Failed");
        }
    }

    private void sendCommand(BufferedWriter out, BufferedReader in, String command) throws IOException {
        out.write(command + "\r\n");
        out.flush();
        System.out.println(">>> " + command);
        readResponse(in);
    }

    private void readResponse(BufferedReader in) throws IOException {
        String response = in.readLine();
        System.out.println("<<< " + response);
    }





    

    // Feature.4 - Record of Sending History
    // The Client saves the details of each sent or scheduled email.
    // There are a table for the sending history in the GUI & also an auto-generated CSV file ("sent-email.csv") for permanent storage.

    private void saveEmailToCSV(String from, String to, String subject, String timestamp, String status) {
        File csvFile = new File(CSV_FILE);
        boolean fileExists = csvFile.exists();

        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile, true))) {
            if (!fileExists) {
                pw.println("From,To,Subject,Timestamp,Status");
            }
            pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n", from, to, subject, timestamp, status);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void History_Load() {
        File csvFile = new File(CSV_FILE);
        if (!csvFile.exists())
            return;

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String[] parts = CSV_content_parsing(line);
                if (parts.length == 5) {
                    sending_history_Model.addRow(parts);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // For parsing CSV lines correctly (handling of commas and quotes).
    // The parsed values are collected into a list and returned as an array.
    private String[] CSV_content_parsing(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean insideQuotes = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                insideQuotes = !insideQuotes;
            } else if (c == ',' && !insideQuotes) {
                result.add(sb.toString());
                sb.setLength(0);
            } else {
                sb.append(c);
            }
        }
        result.add(sb.toString());
        return result.toArray(new String[0]);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SMTPClientJavaGUI().setVisible(true));
    }
}


