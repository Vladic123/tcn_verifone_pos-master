package com.tcn.sdk.liftdemo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static controller.VendApplication.myDB;

/**
 * Settings activity class
 * serial port settings, ip address, admin password, email account etc.
 * @author v.vasilchikov
 */

public class tcn_verifone_SettingsActivity extends AppCompatActivity {

    private EditText ipEdit;
    private EditText portEdit;
    private EditText fSerial;
    private EditText sSerial;
    private EditText fMap;
    private EditText sMap;
    private EditText emailEdit;
    private EditText gstnoEdit;
    private EditText reportUserEdit;
    private EditText reportPasswordEdit;
    private EditText smtpServerEdit;
    private EditText smtpPortEdit;
    private EditText adminPass;

    private String ipaddress;
    private String port;
    private String firstSerial;
    private String secondSerial;
    private String serialMapFirst;
    private String serialMapSecond;
    private String email;
    private String gstno;

    private String reportUser;
    private String reportPassword;
    private String smtpServer;
    private String smtpPort;
    private String adminPassstr;


    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcn_verifone__settings);

        context = this;

        ipaddress = myDB.SettingsGetSingleValue("verifone_ip_address");
        port = myDB.SettingsGetSingleValue("verifone_port");
        firstSerial = myDB.SettingsGetSingleValue("tcn_first_serial");
        secondSerial = myDB.SettingsGetSingleValue("tcn_second_serial");
        serialMapFirst = myDB.SettingsGetSingleValue("tcn_serial_port_group_map_first");
        serialMapSecond = myDB.SettingsGetSingleValue("tcn_serial_port_group_map_second");
        email = myDB.SettingsGetSingleValue("email");
        gstno = myDB.SettingsGetSingleValue("gst");

        reportUser = myDB.SettingsGetSingleValue("reportuser");
        reportPassword = myDB.SettingsGetSingleValue("reportpassword");
        smtpServer = myDB.SettingsGetSingleValue("reportserver");
        smtpPort = myDB.SettingsGetSingleValue("reportport");

        ipEdit = findViewById(R.id.tcn_verifone_settings_verifone_ip_textedit);
        portEdit = findViewById(R.id.tcn_verifone_settings_verifone_port_textedit);
        fSerial = findViewById(R.id.tcn_verifone_settings_tcn_serial_first_serial_port_textedit);
        sSerial = findViewById(R.id.tcn_verifone_settings_tcn_serial_second_serial_port_textedit);
        fMap = findViewById(R.id.tcn_verifone_settings_tcn_serial_port_group_map_first_textedit);
        sMap = findViewById(R.id.tcn_verifone_settings_tcn_serial_port_group_map_second_textedit);
        emailEdit = findViewById(R.id.tcn_verifone_settings_verifone_email_textedit);
        gstnoEdit = findViewById(R.id.tcn_verifone_settings_verifone_gst_textedit);
        adminPass = findViewById(R.id.tcn_verifone_settings_verifone_pass_textedit);

        reportUserEdit = findViewById(R.id.tcn_verifone_settings_report_username_textedit);
        reportPasswordEdit = findViewById(R.id.tcn_verifone_settings_reports_password_textedit);
        smtpServerEdit = findViewById(R.id.tcn_verifone_settings_reports_smtp_server_textedit);
        smtpPortEdit = findViewById(R.id.tcn_verifone_settings_reports_smtp_server_port_textedit);

        adminPass.setText(myDB.SettingsGetSingleValue("adminpassword"));

        ipEdit.setText(ipaddress);
        portEdit.setText(port);
        fSerial.setText(firstSerial);
        sSerial.setText(secondSerial);
        fMap.setText(serialMapFirst);
        sMap.setText(serialMapSecond);
        emailEdit.setText(email);
        gstnoEdit.setText(gstno);
        reportPasswordEdit.setText(reportPassword);
        reportUserEdit.setText(reportUser);
        smtpServerEdit.setText(smtpServer);
        smtpPortEdit.setText(smtpPort);

        Button applyButton = findViewById(R.id.tcn_verifone_settings_apply_button);
        Button exitButton = findViewById(R.id.tcn_verifone_settings_exit_button);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ipaddress = ipEdit.getText().toString();
                port = portEdit.getText().toString();
                firstSerial = fSerial.getText().toString();
                secondSerial = sSerial.getText().toString();
                serialMapFirst = fMap.getText().toString();
                serialMapSecond = sMap.getText().toString();
                email = emailEdit.getText().toString();
                gstno = gstnoEdit.getText().toString();

                reportPassword = reportPasswordEdit.getText().toString();
                reportUser = reportUserEdit.getText().toString();
                smtpPort = smtpPortEdit.getText().toString();
                smtpServer = smtpServerEdit.getText().toString();
                adminPassstr=adminPass.getText().toString();

                myDB.SettingsUpdateSingleValue("verifone_ip_address", ipaddress);
                myDB.SettingsUpdateSingleValue("verifone_port", port);
                myDB.SettingsUpdateSingleValue("tcn_first_serial", firstSerial);
                myDB.SettingsUpdateSingleValue("tcn_second_serial", secondSerial);
                myDB.SettingsUpdateSingleValue("tcn_serial_port_group_map_first", serialMapFirst);
                myDB.SettingsUpdateSingleValue("tcn_serial_port_group_map_second", serialMapSecond);
                myDB.SettingsUpdateSingleValue("email", email);
                myDB.SettingsUpdateSingleValue("gst", gstno);
                myDB.SettingsUpdateSingleValue("reportuser", reportUser);
                myDB.SettingsUpdateSingleValue("reportpassword", reportPassword);
                myDB.SettingsUpdateSingleValue("reportserver", smtpServer);
                myDB.SettingsUpdateSingleValue("reportport", smtpPort);
                myDB.SettingsUpdateSingleValue("adminpassword", adminPassstr);


                Toast.makeText(context, R.string.tcn_verifone_settings_reboot_text, Toast.LENGTH_LONG).show();

            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });




    }
}
