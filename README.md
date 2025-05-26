# A-Basic-Implementation-of-The-SMTP-Client

## Description

This is a **Java-based SMTP Client Application** that uses only **Standard Java Libraries** (`java.net`, `java.io`, `javax.swing`, etc.) for implemented the main functions. It provides a user-friendly **Graphical User Interface (GUI)** to send emails through **MailHog online SMTP server**, and supports the following features:

- **Sending of plain text emails.**
- **Authentication** using `AUTH LOGIN` method.
- Support of **multiple recipients.**
- **Attached of multiple files** of any type.
- **Scheduling of email sending.** 
- **Sending history recording.** 

> 

For a deeper comprehension of the application : <br>
Please read the main source code in the file (SMTPClientJavaGUI.java) with my comments in detail of each important parts .



## 📁 Project Structure
```
 ┣ 📂Project Folder
 ┃ ┣ SMTPClientJavaGUI.java       # Main source code
 ┃ ┣ sent-email.csv               # Auto-generated email history
 ┃ ┣  README.md
```





## Requirements

- Java Development Kit (**JDK 8** or above)
- AN **IDE** that supported Java language  (**Visual Studio Code** recommended).
- **MailHog** installed (https://github.com/mailhog/MailHog) & opening.
- The **MailHog SMTP server** running on 'http://localhost:8025/'.



## How to Run
####
**Recommended**  
Simply, you can click to install the file **[SMTP-Client](https://github.com/lethaian29062004/A-Basic-Implementation-of-An-SMTP-Client/raw/main/SMTP-Client.exe)** & run the application on your computer directly. <br>
***Note*** <br>
If you click on the application after it's installed & get **the warning pop-up from Microsoft Defender SmartScreen**, click on **More info** then **Run anyway** .


####
**Alternative**
- **Open the folder** containing that project in you IDE.
- **Open the Terminal** (Ctrl + Shift + ` in Visual Studio Code), then paste these commands for building the application manually : 
- **javac SMTPClientJavaGUI.java**
- **java SMTPClientJavaGUI**



### Expected Output
- After successfully sending an email, the application automatically logs the details of your sent email into a CSV file named (**sent-email.csv**) .
- Then, you will see **received emails displayed in MailHog** when sent via your app to port 1025.





### Suggested Input Data (for testing)
**From:** Sender@gmail.com  
**To:** Receiver_1@gmail.com, Receiver_2@gmail.com, Receiver_3@gmail.com  
**Subject:** Testing  
**Username:** AKLM10422003  
**Password:** Lt@29062004  
**Content:** This message is used for testing!  
**Attachments:** (Choose any files, any file types)  
**Date & Time:** (Optional - for scheduling)

