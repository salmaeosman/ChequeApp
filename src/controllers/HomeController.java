package controllers;

import javafx.stage.Stage;
import vues.ChequeFiltreView;
import vues.ChequeFormApp;

public class HomeController {

    public void ouvrirFormulaireCheque() {
        try {
            new ChequeFormApp().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ouvrirFiltreCheque() {
        try {
            new ChequeFiltreView().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
