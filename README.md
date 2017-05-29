# NonogramSolver

Nonogram letöltő, fejtő, generáló.

# A program használata:

* A projekt könyvtárában adja ki a következő parancsot: `mvn exec:java`
* A program elindul, megjelenik a grafikus felhasználói felület.
* A felület bal oldalán található gombok segítségéven nyisson meg, importáljon vagy generáljon rejtvényt.
* Sikeres megnyitás esetén a jobb oldali területen láthatja a fájl információit.
* A felület közepén található checkbox-ok segítségével beállíthatja a fejtő és rajzoló tulajdonságait.
* Kattintson a **Start** gombra, és kísérje figyelemmel a rejtvény fejtését.
* A megnyíló ablakban képként exportálhatja az ablak aktuális tartalmát.

# Importálás

* A program a <http://webpbn.com/export.cgi> webhelyről importál rejtvényeket. További információért látogasson el a megadott URL-re.
* Importáláshoz először a <http://webpbn.com>-on böngészve válassza ki a megfejteni kívánt rejtvényt. Az itt található azonosítóra lesz szüksége.
* Az azonosítót (ID) az ablak bal oldalán található **Importálás** gomb melletti mezőbe gépelje be, majd kattintson az **Importálás** gombra.
* A fájl megnyílik, fejthető.
* Amennyiben az oldal valamilyen hibát észlel, a lenti, LOG ablakban olvashatja a hibaüzenetet. 

# Generálás:

* A **Generálás** gombra kattintva megnyílik egy fájlválasztó, itt válasszon ki egy lehetőleg egyszerű mintájú képet.
* A kiválasztás után megnyílik a Generátor ablak.
* Az ablak felső részében állíthatja be a rejtvény paramétereit. A változások azonnal megjelennek a lenti képen.
* A _Méret_ csúszkával a rejtvény méretét allíthatja.
* A _Küszöb_ csúszkával a képre beállított küszöbszintet állíthatja.
* A _Színek invertálása_ jelölővel invertálhatja a színeket.
* A **Generálás!** gombra kattintva a rejtvény generálódik, fejthető.

# Mentés:

Lehetőség van a rejtvények mentésére. Egyelőre csak XML formátumban lehet menteni.

* A mentéshez kattintson a **Mentés** gombra a főablakban.
* A fájlválasztó panel segítségével válassza ki a mentés helyét és a fájl nevét.
* A Mentés gombra kattintva a rejtvény a fájlrendszerbe kerül.

# Jó szórakozást!