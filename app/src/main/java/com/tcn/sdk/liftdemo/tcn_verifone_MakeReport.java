package com.tcn.sdk.liftdemo;

import android.os.AsyncTask;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import static controller.VendApplication.myDB;

/**
 * Report class
 *  it makes reports and send it vie email
 *
 * @author v.vasilchikov
 */

class tcn_verifone_MakeReport extends AsyncTask<Void, Void, Void> {

    private OnTaskCompleted listener;

    tcn_verifone_MakeReport(OnTaskCompleted listener) {
        this.listener = listener;
    }

    /** send generated report from disk via gmail smtp with tls */
    public static void GmailSMTPviaTLS() {

        final String username = myDB.SettingsGetSingleValue("reportuser");//"poggeetest@gmail.com";
        final String password = myDB.SettingsGetSingleValue("reportpassword");//"Ghjcnjrdfif12";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", myDB.SettingsGetSingleValue("reportserver"));//"smtp.gmail.com");
        prop.put("mail.smtp.port", myDB.SettingsGetSingleValue("reportport"));//"587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            session.setDebug(true);
            session.getDebugOut();

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("poggeetest@gmail.com"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(myDB.SettingsGetSingleValue("email"))
            );
            message.setSubject("Poggee machine report");
            message.setText("Hello! Here is our report for you!");

            MimeBodyPart messageBodyPart = new MimeBodyPart();
            String exStoragePath = System.getenv("EXTERNAL_STORAGE");

            DataSource source = new FileDataSource(exStoragePath + "/Download/report.xls");
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(exStoragePath + "/Download/report.xls");

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);

            Transport.send(message);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setListener(OnTaskCompleted listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        listener.taskCompleted();


    }

    @Override
    protected Void doInBackground(Void... params) {

        MakeReport();
        return null;
    }

    public interface OnTaskCompleted {
        void taskCompleted();
    }

    /** generate report in xls format and store it on the disk*/
    public void MakeReport() {
        String exStoragePath = System.getenv("EXTERNAL_STORAGE");

        File file = new File(exStoragePath + "/Download/report.xls");
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("report");


        Integer rows = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_rows_main"));
        Integer lanesPerRow = Integer.parseInt(myDB.SettingsGetSingleValue("tcn_lanes_per_row_main"));

        Integer laneNumber = 1;
        for (int row = 1; row <= rows; row++) {
            Row rowO = sheet.createRow(row);
            for (int lane = 1; lane <= lanesPerRow; lane++) {
                if ((laneNumber % 10) != 0) {
                    tcn_verifone_AuxLane AL = myDB.LanesGetById(laneNumber * 1L);
                    Cell amount = rowO.createCell(lane);
                    amount.setCellValue(AL.getAmount());
                }
                laneNumber++;

            }
        }


        // Записываем всё в файл
        try {
            book.write(new FileOutputStream(file));
            book.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SendMail();

    }

    private void SendMail() {

        GmailSMTPviaTLS();
    }

}

