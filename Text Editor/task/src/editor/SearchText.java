package editor;

import javax.swing.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchText {
    private String foundText = null;
    private int currentIndex;
    private boolean useRegex;
    private JTextArea textArea;

    class Position {
        int startPos;
        int endPos;

        public Position(int start, int end) {
            startPos = start;
            endPos = end;
        }
    }
    //private final Deque<Position> positions = new ArrayDeque<>();
    private final ArrayList<Position> positions = new ArrayList<>();

    public synchronized void  setUseRegex(boolean useRegex) {
        this.useRegex = useRegex;
    }

    public void setTextArea(JTextArea textArea) {
        this.textArea = textArea;
    }

    private void selectFoundText(int index) {
        if (positions.isEmpty())
            return;

        if (index < 0)
            currentIndex = positions.size() - 1;
        else if (index >= positions.size())
            currentIndex = 0;
        else
            currentIndex = index;

        Position pos = positions.get(currentIndex);
        textArea.setCaretPosition(pos.endPos);
        textArea.select(pos.startPos, pos.endPos);
        textArea.grabFocus();
    }

    private void saveSearchResults() {
        positions.clear();
        int start = -1, end = -1;

        if (useRegex) {
            Matcher matcher = Pattern.compile(foundText).matcher(textArea.getText());
            while (matcher.find()) {
                positions.add(new Position(matcher.start(), matcher.end()));
            }
        } else {
            int index = textArea.getText().indexOf(foundText);
            int len = foundText.length();
            while (index != -1) {
                positions.add(new Position(index, index + len));
                index = textArea.getText().indexOf(foundText, index + len);
            }
        }
    }

    public synchronized void search(String text) {
         if (text.isEmpty()) {
            return;
        }

        foundText = text;
        saveSearchResults();
        selectFoundText(0);
    }

    public synchronized void next() {
        selectFoundText(++currentIndex);
    }

    public synchronized void prev() {
        selectFoundText(--currentIndex);
    }
}
