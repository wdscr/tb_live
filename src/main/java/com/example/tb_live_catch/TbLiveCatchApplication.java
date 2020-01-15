package com.example.tb_live_catch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@SpringBootApplication
public class TbLiveCatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(TbLiveCatchApplication.class, args);
        System. setProperty("java.awt.headless", "false");
        new JFrameA();
    }

    static class JFrameA extends JFrame {

        public JFrameA() {

            setTitle("淘口令抓取");
            setSize(400, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            JLabel label = new JLabel("输入淘口令/选择文件");
            JTextField tf1 = new JTextField();
            tf1.addFocusListener(new TextFieldHintListener(tf1, "提示文字..."));
            tf1 = new JTextField();
            tf1.setBounds(239, 57, 243, 34);
            String name="请输入新账号(限数字)";
            add(tf1);
            Container c = getContentPane();
            c.add(label);
            setVisible(true);
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
    }

    static class TextFieldHintListener implements FocusListener {

        private String hintText;
        private JTextField textField;

        public TextFieldHintListener(JTextField TextField, String hintText) {
            this.textField = TextField;
            this.hintText = hintText;
            TextField.setText(hintText);  //默认直接显示
            TextField.setForeground(Color.GRAY);
        }

        @Override
        public void focusGained(FocusEvent e) {
            //获取焦点时，清空提示内容
            String temp = textField.getText();
            if (temp.equals(hintText)) {
                textField.setText("");
                textField.setForeground(Color.BLACK);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            //失去焦点时，没有输入内容，显示提示内容
            String temp = textField.getText();
            if (temp.equals("")) {
                textField.setForeground(Color.GRAY);
                textField.setText(hintText);
            }
        }
    }

}
