import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static java.lang.Math.max;

public class Main {
    static boolean wrongDep = false;
    static Map<String, Data> mapOfFiles = new HashMap<>();
    static void fileWork(File file, String address) throws FileNotFoundException {
        Data fileData = new Data();
        Scanner scanner = new Scanner(file);
        fileData.name = address + file.getName();
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            fileData.text += line + "\n";
            if (line.contains("require")) {
                String dep = line.substring(line.indexOf("require") + 9, line.length() - 1) + ".txt";
                fileData.dependence.add(dep);
            }
        }
        mapOfFiles.put(fileData.name, fileData);
    }
    /**
    Проверка файла на директорию, считывание всех файлов в папке и рекурсивынй переход к вложенным
     */
    static void folderScanner(File folder, String address) throws FileNotFoundException {
        for(File importFile : Objects.requireNonNull(folder.listFiles())) {
            if (importFile.isDirectory()) {
                address += importFile.getName() + '/';
                folderScanner(importFile, address);
                address = address.substring(address.lastIndexOf('/') + 1, address.length());
            } else {
                fileWork(importFile, address);
            }
        }
    }
    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
        File rootFolder = new File(in.nextLine());
        String address = "";
        folderScanner(rootFolder, address);
        for (Data files : mapOfFiles.values()) {
            for (String i : files.dependence) {
                findPar(i, files.name);
            }
        }
        for (Data files : mapOfFiles.values()) {
            checkDep(files);
        }
        if (wrongDep) {
            System.exit(1);
        }
        for (Data files : mapOfFiles.values()) {
            for (String i : files.dependence) {
                files.deepOfDep = max(countDeep(mapOfFiles.get(i)), files.deepOfDep);
            }
        }

        int isPrinted = 0;
        int deep = 0;
        while (isPrinted != mapOfFiles.size()) {
            for (Data file : mapOfFiles.values()) {
                if (file.deepOfDep == deep) {
                    isPrinted++;
                    System.out.println(file.text);
                }
            }
            deep++;
        }
    }

    /**
     Проверка зависимостей
     */
    static void checkDep(Data file) {
        if (!file.child.isEmpty()) {
            for (String child : file.child) {
                checkParDep(mapOfFiles.get(child), file.name);
            }
        }
    }
    /**
    Рекурсивная проверка зависимостей детей
     */
    static void checkParDep(Data file, String name) {
        if (file.child.contains(name)) {
            System.out.println(name + " " + file.name);
            wrongDep = true;
        } else {
            if (!file.child.isEmpty()) {
                for (String child : file.child) {
                    checkParDep(mapOfFiles.get(child), name);
                }
            }
        }
    }
    /**
    Подсчет высоты (максимальный длины зависимостей) файла
     */
    static int countDeep(Data file) {
        int cnt = 0;
        if (!file.dependence.isEmpty()) {
            for (String i : file.dependence) {
                findPar(i, file.name);
                cnt = max(countDeep(mapOfFiles.get(i)), file.deepOfDep);
            }
        }
        return cnt + 1;
    }
    /**
    Добавление детей
     */
    static void findPar(String parent, String child) {
        mapOfFiles.get(parent).child.add(child);
    }

}