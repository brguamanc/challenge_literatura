package com.byron.app.repositorio;

import com.byron.app.model.Autor;
import com.byron.app.model.Idiomas;
import com.byron.app.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LibrosRepositorio extends JpaRepository<Libro, Long> {

    Optional<Libro> findByTituloContainsIgnoreCase(String titulo);

    List<Libro> findByIdiomas(Idiomas idiomasEnum);

    @Query("SELECT DISTINCT l.autor FROM Libro l")
    List<Autor> findAllUniqueAutores();

    @Query("SELECT DISTINCT l.autor FROM Libro l WHERE l.autor.anioNacimiento <= :anio AND (l.autor.anioFallecimiento IS NULL OR l.autor.anioFallecimiento > :anio)")
    List<Autor> findByYearAutores(@Param("anio") int anio );
}
