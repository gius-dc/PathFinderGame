import java.util.List;

public class dataGUI {
        private char[][] lab;
    List<Oggetto> oggetti;

        public dataGUI(char[][] lab, List<Oggetto> oggetti) {
            this.lab = lab;
            this.oggetti = oggetti;
        }

        public char[][] getLab() {
            return lab;
        }

        public List<Oggetto> getObj() {
            return oggetti;
        }
}
