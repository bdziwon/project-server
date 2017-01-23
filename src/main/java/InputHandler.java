
public class InputHandler {

    public Object handle(Object object) {
        //case...
        return object;
    }

    private Object insert(Object object) {
            // to stworzy dowolny projekt, błąd lub użytkownika, wystarczy żeby rozróżnić że to insert
            DatabaseServer db = DatabaseServer.getInstance();
            object = db.insert(object);

            //zwraca obiekt z id z bazy, nie dodaje issue i user do projektu jeśli na ma liście, to robi dopiero update dla projektu
            return object;
    }

    private Object update(Object object) {
        //update
        //jeśli aktualizujemy projekt to stworzą się automatycznie nieistniejący użytkownicy i błędy
        DatabaseServer db = DatabaseServer.getInstance();
        object = db.update(object);
        return object;
    }

    /**
     * Usuwanie z bazy po id w obiekcie
     * Jeśli usuwamy projekt usunie też jego błędy, użytkownicy zostają
     * @param object obiekt klasy {@link Issue} {@link Project} {@link User}
     * @return ilość zmian w bazie, jak zero to błąd
     */
    private Object delete(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        return db.delete(object);
    }

    /**
     * Pobiera dane z bazy do obiektu
     * Jeśli to projekt, pobierze także wszystkie błędy i użytkowników do list
     * @param object
     * @return Obiekt reprezentujący dane z bazy
     */
    private Object select(Object object) {
        DatabaseServer db = DatabaseServer.getInstance();
        return db.select(object);
    }
    //TODO: Case z rozpoznywaniem operacji
    //TODO: zapis projektu (plików) na serwerze
    //TODO: String w żadnym obiekcie nie może zawierac apostrofu '
}
