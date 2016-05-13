package main;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.*;

public class Controller {
    private View view;
    private HTMLDocument document;
    private File currentFile;

    public Controller(View view) {
        this.view = view;
    }

    public HTMLDocument getDocument() {
        return document;
    }

    public String getPlainText() {
        StringWriter stringWriter = new StringWriter();
        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        try {
            htmlEditorKit.write(stringWriter, document, 0, document.getLength());
        }
        catch (IOException | BadLocationException e) {
            ExceptionHandler.log(e);
        }
        return stringWriter.toString();
    }

    public void setPlainText(String text) {
        try {
            resetDocument();
            StringReader stringReader = new StringReader(text);
            HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
            htmlEditorKit.read(stringReader, document, 0);
        }
        catch (IOException | BadLocationException e) {
            ExceptionHandler.log(e);
        }
    }

    public void resetDocument() {
        if (document != null) {
            document.removeUndoableEditListener(view.getUndoListener());
        }

        HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
        document = (HTMLDocument) htmlEditorKit.createDefaultDocument();
        document.addUndoableEditListener(view.getUndoListener());
        view.update();
    }

    public void init() {
        createNewDocument();
    }

    public void exit() {
        System.exit(0);
    }

    public static void main(String[] args) {
        View view = new View();
        Controller controller = new Controller(view);
        view.setController(controller);
        view.init();
        controller.init();
    }

    public void createNewDocument() {
        view.selectHtmlTab();
        resetDocument();
        view.setTitle("HTML редактор");
        view.resetUndo();
        currentFile = null;
    }

    public void openDocument() {
        try {
            view.selectHtmlTab();
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new HTMLFileFilter());
            int choose = jFileChooser.showOpenDialog(view);
            if (choose == JFileChooser.APPROVE_OPTION) {
                currentFile = jFileChooser.getSelectedFile();
                resetDocument();
                view.setTitle(currentFile.getName());
                try (FileReader fileReader = new FileReader(currentFile)) {
                    HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                    htmlEditorKit.read(fileReader, document, 0);
                    view.resetUndo();
                }
            }
        }
        catch (IOException | BadLocationException e) {
            ExceptionHandler.log(e);
        }
    }

    public void saveDocument() {
        if (currentFile == null) {
            saveDocumentAs();
        } else {
            view.selectHtmlTab();
            view.setTitle(currentFile.getName());
            try (FileWriter fileWriter = new FileWriter(currentFile)) {
                HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                htmlEditorKit.write(fileWriter, document, 0, document.getLength());
            }
            catch (IOException | BadLocationException e) {
                ExceptionHandler.log(e);
            }
        }
    }


    public void saveDocumentAs() {
        try {
            view.selectHtmlTab();
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileFilter(new HTMLFileFilter());
            int choose = jFileChooser.showSaveDialog(view);
            if (choose == JFileChooser.APPROVE_OPTION) {
                currentFile = jFileChooser.getSelectedFile();
                view.setTitle(currentFile.getName());
                try (FileWriter fileWriter = new FileWriter(currentFile)) {
                    HTMLEditorKit htmlEditorKit = new HTMLEditorKit();
                    htmlEditorKit.write(fileWriter, document, 0, document.getLength());
                }
            }
        }
        catch (IOException | BadLocationException e) {
            ExceptionHandler.log(e);
        }
    }
}
