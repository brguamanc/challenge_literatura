package com.byron.app.principal;

import com.byron.app.model.*;
import com.byron.app.repositorio.LibrosRepositorio;
import com.byron.app.service.ConsumoAPI;
import com.byron.app.service.ConvierteDatos;

import java.util.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private Scanner sc = new Scanner(System.in);
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosLibro> datosLibros = new ArrayList<>();
    private List<Libro> libros;
    private List<Autor> autor;


    private LibrosRepositorio repositorio;
    public Principal(LibrosRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void muestraElMenu() {

        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    *** SELECCIONE UNA OPCIÓN DEL MENU *** 
                    1 - Buscar de libro por título 
                    2 - Listar libros registrados
                    3 - Listar autores registrados
                    4 - Listar autores vivos en un determinado año
                    5 - Listar libros por idioma                                 
                    0 - Salir
                    """;
            System.out.println(menu);

                opcion = sc.nextInt();
                sc.nextLine();

                switch (opcion) {
                    case 1:
                        buscarLibroWeb();
                        break;
                    case 2:
                        mostrarLibrosBuscados();
                        break;
                    case 3:
                        listaDeAutores();
                        break;
                    case 4:
                        autoresPorAnio();
                        break;
                    case 5:
                        buscarLibroPorIdioma();
                        break;
                    case 0:
                        System.out.println("Cerrando la aplicación...");
                        break;
                    default:
                        System.out.println("Opción inválida");
                }
            }
    }


    private DatosLibro getDatosLibros() {
        System.out.println("Ingrese el nombre del libro a buscar");
        var libro = sc.nextLine();
        var json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + libro.replace(" ", "%20"));

        Datos datos = conversor.obtenerDatos(json, Datos.class);
        List<DatosLibro> libros = datos.resultados();

        if (!libros.isEmpty()) {
            return libros.get(0);
        } else {
            return null;
        }
    }
    private void buscarLibroWeb() {
        DatosLibro datos = getDatosLibros();
        if(datos != null) {
            Optional<Libro> tituloYaExiste = repositorio.findByTituloContainsIgnoreCase(datos.titulo());

            if(tituloYaExiste.isPresent()){
                System.out.println("El libro ya existe en la base de datos ");
            } else{
                Libro libro = new Libro(datos);
                repositorio.save(libro);
                System.out.println("Libro agregado a base de datos ");
                System.out.printf("""
                    Titulo: %s
                    Idioma: %s
                    Numero de descargas: %s
                    Autor: %s ( %s - %s ) \n
                    """, datos.titulo(), Idiomas.fromString(datos.idiomas().get(0)).getIdiomaEnEspañol(), datos.numeroDeDescargas(),
                        datos.autor().get(0).nombreAutor(),
                        (datos.autor().get(0).anioNacimiento() != null ) ? datos.autor().get(0).anioNacimiento(): "fecha de nacimiento desconocida",
                        (datos.autor().get(0).anioFallecimiento() != null ) ? datos.autor().get(0).anioFallecimiento() : " " );
                System.out.println(" *** *** *** *** \n");

            }

        } else {
            System.out.println("\n *** No se encontró el libro o el autor que intentas buscar *** \n");
        }

        // System.out.println("datos "+datos);

    }

    private void mostrarLibrosBuscados() {

        libros = repositorio.findAll();
        if (libros.isEmpty()) {
            System.out.println("\n *** No hay libros en la base de datos *** \n");
        } else {
            System.out.println("\n *** *** *** *** ");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getTitulo))
                    .forEach(l -> System.out.printf("""
                                Libro: %s
                                Idioma: %s
                                Numero de descargas: %s
                                Autor: %s \n
                                """, l.getTitulo(), l.getIdiomas().getIdiomaEnEspañol(), l.getNumeroDeDescargas(), l.getAutor().getNombreAutor()));
            System.out.println(" *** *** *** *** \n");
        }

    }

    private void buscarLibroPorIdioma(){

        System.out.println("\n *** Seleccione el idioma del libro que desea buscar *** \n");
        System.out.println("1. Español");
        System.out.println("2. Inglés");
        System.out.println("3. Portugués");
        System.out.println("4. Francés");
        System.out.println("5. Italiano");
        System.out.print("\n  Ingrese el número de la opción deseada:  ");
        try {
            int opcion = Integer.parseInt(sc.nextLine());
            Idiomas idiomaSeleccionado;

            switch (opcion) {
                case 1:
                    idiomaSeleccionado = Idiomas.ES;
                    break;
                case 2:
                    idiomaSeleccionado = Idiomas.EN;
                    break;
                case 3:
                    idiomaSeleccionado = Idiomas.PT;
                    break;
                case 4:
                    idiomaSeleccionado = Idiomas.FR;
                    break;
                case 5:
                    idiomaSeleccionado = Idiomas.IT;
                    break;
                default:
                    System.out.println("Opción no válida. Se utilizará español por defecto.");
                    idiomaSeleccionado = Idiomas.ES;
            }

            List<Libro> librosPorIdioma = repositorio.findByIdiomas(idiomaSeleccionado);

            if (librosPorIdioma.isEmpty()) {
                System.out.println("\n *** No se encontraron libros en el idioma seleccionado: " + idiomaSeleccionado.name() + " *** \n");
            } else {
                System.out.println("\n *** Libros encontrados en (" + idiomaSeleccionado.getIdiomaEnEspañol() + ") *** \n");
                librosPorIdioma.forEach(libro -> System.out.printf("""
                                Titulo: %s
                                Autor: %s \n
                                """,
                        libro.getTitulo(), libro.getAutor().getNombreAutor()));
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada no válida. Por favor, ingrese un número entre 1 y 5.");
        }
    }

    private void listaDeAutores(){
        autor = repositorio.findAllUniqueAutores();

        if (autor.isEmpty()) {
            System.out.println("\n *** No se encontraron autores en la base de datos *** \n");
        } else {
            autor.forEach(autor -> {
                String titulos = autor.getLibros().stream()
                        .map(Libro::getTitulo)
                        .collect(Collectors.joining(", "));
                System.out.printf(
                        """                                      
                        Autor: %s ( %s - %s )
                        Titulos: %s \n
                        """, autor.getNombreAutor(),
                        autor.getAnioNacimiento(), autor.getAnioFallecimiento(), titulos);
            });
        }

    }
    private void autoresPorAnio(){
        System.out.print("\n *** Ingrese el año para buscar autores vivos:  *** \n");
        int anio = Integer.parseInt(sc.nextLine());
        autor = repositorio.findByYearAutores(anio);

        if(autor.isEmpty()){
            System.out.println("\n *** No se encontraron autores vivos en el año " + anio + " *** \n");
        }else{
            System.out.println("\n *** Autores vivos en el año " + anio + " *** \n");
            autor.forEach(autor -> {
                String estadoVital = autor.getAnioFallecimiento() == null ?
                        "Aún vivo" :
                        "Fallecido en " + autor.getAnioFallecimiento();
                System.out.println("- " + autor.getNombreAutor() +
                        " (Nacido en: " + autor.getAnioNacimiento() +
                        ", " + estadoVital + ") \n");
            });
        }
    }
}
