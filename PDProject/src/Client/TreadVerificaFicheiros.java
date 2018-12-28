/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

import java.nio.file.WatchService;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Pedro
 */
public class TreadVerificaFicheiros extends Thread {

    private Cliente cliente;

    public TreadVerificaFicheiros(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public void run() {
        System.out.println(" vou comecar tread verifica Ficheiros:");
        try (WatchService service = FileSystems.getDefault().newWatchService()) {
            System.out.println(" entrei");
            Map<WatchKey, Path> keyMap = new HashMap<>();
            System.out.println("passei aqui 1:");
            Path path = Paths.get("C:\\Users\\Pedro\\Documents\\NetBeansProjects\\PD\\PD\\PDProject\\Ficheiros");
            System.out.println("passei aqui 2:");
            keyMap.put(path.register(service, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_MODIFY), path);
            WatchKey watchKey;

            do {
                watchKey = service.take();
                Path eventDir = keyMap.get(watchKey);

                for (WatchEvent<?> event : watchKey.pollEvents()) {
                    WatchEvent.Kind<?> Kind = event.kind();
                    Path evenPath = (Path) event.context();
                    System.out.println(eventDir + ": " + Kind + ":" + evenPath);

                    if (Kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("enviar po servidor que quero disponoblizar ficheiro");
                        cliente.DisponiblizarFicheiro(evenPath.toString(), eventDir.toString());
                    } else if (Kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("enviar po servidor que quero eleminar um ficheiro disponiblizado ");
                        cliente.EleminarFicheiroDisponablizado(evenPath.toString());
                    }
                }
            } while (watchKey.reset());

        } catch (Exception e) {
            System.out.println("erro aqui na tread verifica Ficheiro");
        }
    }

}
