# A-Basic-Implementation-of-An-SMTP-Client

## 🔍 Description

This is a **Java-based SMTP Client application** that uses only **standard Java libraries** (`java.net`, `java.io`, `javax.swing`, etc.) for implemented. It provides a user-friendly **Graphical User Interface (GUI)** to send emails through MailHog online SMTP server, and supports the following features:

- Sending of plain text emails.
- **Authentication** using `AUTH LOGIN` method.
- Support of **multiple recipients**.
- **Attached of multiple files** of any type.
- **Scheduling of email sending** 
- **Sending history recording** 

> 

For a deeper comprehension of the application, please read the main source code in the file (SMTPClientJavaGUI.java) with my comments in detail of each important parts .



## 📁 Project Structure
```
 ┣ 📂Project Folder
 ┃ ┣ SMTPClientJavaGUI.java       # Main source code
 ┃ ┣ sent-email.csv               # Auto-generated email history
 ┃ ┣  README.md
```



## 🖥️ How to Use

### ✅ Requirements

- Java Development Kit (**JDK 8** or above)
- AN IDE that supported Java language  (Visual Studio Code recommended).
- MailHog installed (https://github.com/mailhog/MailHog) & opening.
- The MailHog SMTP server running on 'http://localhost:8025/'.



### ▶️ Build & Run

- Open the Terminal (Ctrl + Shift + ` in Visual Studio Code), then paste these commands : 

- javac SMTPClientJavaGUI.java
- java SMTPClientJavaGUI



### Expected Output
- After successfully sending an email, the application automatically logs the details of your sent email into a CSV file named ('sent-email.csv') .
- Then, you will see received emails displayed in MailHog when sent via your app to port 1025.





### Suggested Input Data (for testing)
From: Sender@gmail.com  
To: Receiver_1@gmail.com, Receiver_2@gmail.com, Receiver_3@gmail.com  
Subject: Testing  
Username: AKLM10422003  
Password: Lt@29062004  
Content: This message is used for testing!  
Attachments: (Choose any files, any file types)  
Date & Time: (Optional - for scheduling)

