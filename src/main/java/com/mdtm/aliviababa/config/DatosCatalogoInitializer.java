package com.mdtm.aliviababa.config;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.mdtm.aliviababa.modelo.CategoriaEntity;
import com.mdtm.aliviababa.modelo.ProductoEntity;
import com.mdtm.aliviababa.repository.CategoriaRepository;
import com.mdtm.aliviababa.repository.ProductoRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatosCatalogoInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatosCatalogoInitializer.class);

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    @Override
    @Transactional
    public void run(String... args) {
        Map<String, CategoriaEntity> categorias = cargarCategorias();
        asegurarCategorias(categorias);

        Map<String, ProductoEntity> productosExistentes = new HashMap<>();
        for (ProductoEntity producto : productoRepository.findAll()) {
            productosExistentes.putIfAbsent(clave(producto.getNombre()), producto);
        }

        List<ProductoEntity> productosParaGuardar = new ArrayList<>();
        int productosNuevos = 0;
        int productosActualizados = 0;

        for (ProductoMuestra muestra : productosMuestra()) {
            ProductoEntity existente = productosExistentes.get(clave(muestra.nombre()));
            CategoriaEntity categoria = categorias.get(clave(muestra.categoria()));

            if (existente == null) {
                ProductoEntity nuevo = ProductoEntity.builder()
                    .nombre(muestra.nombre())
                    .descripcion(muestra.descripcion())
                    .precio(muestra.precio())
                    .stock(muestra.stock())
                    .imagenUrl(muestra.imagenUrl())
                    .categoria(categoria)
                    .build();
                productosParaGuardar.add(nuevo);
                productosExistentes.put(clave(muestra.nombre()), nuevo);
                productosNuevos++;
                continue;
            }

            boolean actualizado = false;
            if (existente.getImagenUrl() == null
                || existente.getImagenUrl().isBlank()
                || imagenNecesitaCorreccion(existente.getImagenUrl())) {
                existente.setImagenUrl(muestra.imagenUrl());
                actualizado = true;
            }
            if (existente.getCategoria() == null && categoria != null) {
                existente.setCategoria(categoria);
                actualizado = true;
            }
            if (actualizado) {
                productosParaGuardar.add(existente);
                productosActualizados++;
            }
        }

        if (!productosParaGuardar.isEmpty()) {
            productoRepository.saveAll(productosParaGuardar);
        }

        log.info(
            "Catálogo verificado: {} productos nuevos y {} productos existentes completados",
            productosNuevos,
            productosActualizados
        );
    }

    private Map<String, CategoriaEntity> cargarCategorias() {
        Map<String, CategoriaEntity> categorias = new HashMap<>();
        for (CategoriaEntity categoria : categoriaRepository.findAll()) {
            categorias.putIfAbsent(clave(categoria.getNombre()), categoria);
        }
        return categorias;
    }

    private void asegurarCategorias(Map<String, CategoriaEntity> categorias) {
        List<String> nombres = List.of(
            "Electrónicos",
            "Hogar",
            "Papelería",
            "Computación",
            "Audio y video",
            "Cocina",
            "Oficina",
            "Deportes y fitness",
            "Belleza y cuidado personal"
        );

        for (String nombre : nombres) {
            String clave = clave(nombre);
            if (categorias.containsKey(clave)) {
                continue;
            }

            CategoriaEntity nueva = new CategoriaEntity();
            nueva.setNombre(nombre);
            categorias.put(clave, categoriaRepository.save(nueva));
        }
    }

    private List<ProductoMuestra> productosMuestra() {
        return List.of(
            new ProductoMuestra(
                "Watch 4",
                "Reloj inteligente para actividad diaria y notificaciones.",
                3499.00,
                20,
                "Electrónicos",
                "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "SmartPhone",
                "Teléfono inteligente de alto rendimiento para uso diario.",
                8999.00,
                15,
                "Electrónicos",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Galaxy s24 Ultra",
                "Smartphone premium con pantalla amplia y cámara avanzada.",
                24999.00,
                10,
                "Electrónicos",
                "https://images.unsplash.com/photo-1598327105666-5b89351aff97?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Laptop ultraligera 14 pulgadas",
                "Equipo portátil de alto desempeño, ideal para trabajo, estudio y movilidad.",
                18999.00,
                18,
                "Computación",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Teclado mecánico inalámbrico",
                "Teclado compacto con conexión Bluetooth y respuesta táctil precisa.",
                1299.00,
                35,
                "Computación",
                "https://images.unsplash.com/photo-1587829741301-dc798b83add3?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Audífonos Bluetooth con cancelación de ruido",
                "Audio envolvente, micrófono integrado y batería para todo el día.",
                1799.00,
                42,
                "Audio y video",
                "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Bocina portátil resistente al agua",
                "Sonido potente en un diseño compacto para interiores y exteriores.",
                1099.00,
                30,
                "Audio y video",
                "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Cafetera programable de 12 tazas",
                "Prepara café automáticamente y conserva la temperatura por más tiempo.",
                1249.00,
                24,
                "Cocina",
                "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Licuadora de vaso de 1.5 litros",
                "Motor de alta potencia y cuchillas de acero para preparaciones uniformes.",
                1099.00,
                20,
                "Cocina",
                "https://images.unsplash.com/photo-1570222094114-d054a817e56b?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Silla ergonómica de oficina",
                "Soporte lumbar ajustable y asiento cómodo para jornadas prolongadas.",
                3899.00,
                12,
                "Oficina",
                "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Lámpara LED de escritorio",
                "Iluminación regulable con diseño moderno y bajo consumo de energía.",
                649.00,
                40,
                "Oficina",
                "https://images.unsplash.com/photo-1507473885765-e6ed057f782c?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Tapete de yoga antideslizante",
                "Superficie acolchada y firme para yoga, estiramientos y ejercicios en casa.",
                549.00,
                28,
                "Deportes y fitness",
                "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Mancuernas ajustables de 20 kg",
                "Sistema de peso graduable para rutinas completas de fuerza.",
                2799.00,
                10,
                "Deportes y fitness",
                "https://images.unsplash.com/photo-1583454110551-21f2fa2afe61?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Secadora iónica profesional",
                "Secado rápido con control de temperatura y reducción de frizz.",
                899.00,
                25,
                "Belleza y cuidado personal",
                "https://images.unsplash.com/photo-1522338140262-f46f5913618a?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Kit de cuidado facial",
                "Selección de productos para limpieza, hidratación y cuidado cotidiano.",
                749.00,
                32,
                "Belleza y cuidado personal",
                "https://images.unsplash.com/photo-1556228720-195a672e8a03?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Tableta de 10 pulgadas",
                "Pantalla de alta resolución, batería de larga duración y diseño ligero para entretenimiento y estudio.",
                6499.00,
                22,
                "Electrónicos",
                "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Cámara instantánea compacta",
                "Cámara portátil con impresión instantánea para conservar recuerdos en segundos.",
                2499.00,
                17,
                "Electrónicos",
                "https://images.unsplash.com/photo-1526170375885-4d8ecf77b99f?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Reloj analógico minimalista",
                "Reloj de diseño clásico con correa cómoda y acabado versátil para uso cotidiano.",
                1399.00,
                26,
                "Electrónicos",
                "https://images.unsplash.com/photo-1524592094714-0f0654e20314?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Aspiradora ciclónica sin bolsa",
                "Sistema de succión eficiente, depósito lavable y accesorios para diferentes superficies.",
                2299.00,
                14,
                "Hogar",
                "https://images.unsplash.com/photo-1558317374-067fb5f30001?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Juego de sábanas matrimonial",
                "Conjunto suave y transpirable con sábana plana, ajustable y fundas para almohada.",
                699.00,
                34,
                "Hogar",
                "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Sillón decorativo para sala",
                "Asiento cómodo de líneas modernas para complementar salas, recámaras o espacios de lectura.",
                4299.00,
                8,
                "Hogar",
                "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Lámpara decorativa de mesa",
                "Iluminación cálida con diseño contemporáneo para burós, recibidores y mesas auxiliares.",
                799.00,
                29,
                "Hogar",
                "https://images.unsplash.com/photo-1540932239986-30128078f3c5?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Cuaderno profesional de pasta dura",
                "Cuaderno resistente con hojas rayadas, separador y cierre elástico para notas diarias.",
                189.00,
                65,
                "Papelería",
                "https://images.unsplash.com/photo-1455390582262-044cdead277a?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Set de lápices de colores",
                "Selección de colores intensos con mina resistente para dibujo, escuela y proyectos creativos.",
                329.00,
                48,
                "Papelería",
                "https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Agenda semanal 2026",
                "Organización por semanas, espacio para objetivos y secciones de notas personales.",
                249.00,
                52,
                "Papelería",
                "https://images.unsplash.com/photo-1506784983877-45594efa4cbe?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Mochila escolar impermeable",
                "Compartimentos acolchados, tirantes ajustables y material resistente al agua.",
                899.00,
                31,
                "Papelería",
                "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Mouse ergonómico inalámbrico",
                "Diseño cómodo, seguimiento preciso y conexión inalámbrica para oficina o estudio.",
                649.00,
                44,
                "Computación",
                "https://images.unsplash.com/photo-1527814050087-3793815479db?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Monitor LED de 24 pulgadas",
                "Panel Full HD con marcos delgados y conexiones versátiles para trabajo y entretenimiento.",
                3299.00,
                19,
                "Computación",
                "https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Cámara web Full HD",
                "Videollamadas nítidas con micrófono integrado, enfoque automático y montaje universal.",
                899.00,
                27,
                "Computación",
                "https://images.unsplash.com/photo-1587825140708-dfaf72ae4b04?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Bocina doméstica de alta fidelidad",
                "Sonido definido y potente en un diseño ideal para salas, estudios y habitaciones.",
                2499.00,
                16,
                "Audio y video",
                "https://images.unsplash.com/photo-1545454675-3531b543be5d?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Micrófono USB para streaming",
                "Captura de voz clara, control de ganancia y base estable para transmisiones y videollamadas.",
                1399.00,
                23,
                "Audio y video",
                "https://images.unsplash.com/photo-1590602847861-f357a9332bbc?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Tornamesa con conexión Bluetooth",
                "Reproduce discos de vinilo y transmite audio de forma inalámbrica con estilo clásico.",
                3499.00,
                11,
                "Audio y video",
                "https://images.unsplash.com/photo-1461360228754-6e81c478b882?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Procesador de alimentos compacto",
                "Equipo práctico para mezclar, triturar y preparar ingredientes en pocos minutos.",
                1599.00,
                21,
                "Cocina",
                "https://images.unsplash.com/photo-1585515320310-259814833e62?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Juego de cuchillos de acero",
                "Cuchillos de cocina con filo duradero, mangos ergonómicos y soporte organizador.",
                799.00,
                25,
                "Cocina",
                "https://images.unsplash.com/photo-1593618998160-e34014e67546?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Báscula digital de cocina",
                "Medición precisa con pantalla digital, función de tara y diseño fácil de limpiar.",
                349.00,
                38,
                "Cocina",
                "https://images.unsplash.com/photo-1594736797933-d0501ba2fe65?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Escritorio minimalista con cajón",
                "Superficie amplia y estructura estable para crear un espacio de trabajo ordenado.",
                2799.00,
                13,
                "Oficina",
                "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Archivador metálico de tres cajones",
                "Almacenamiento seguro y resistente para documentos, carpetas y suministros de oficina.",
                2399.00,
                9,
                "Oficina",
                "https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Calculadora de escritorio",
                "Pantalla amplia, teclas cómodas y funciones esenciales para escuela, negocio y oficina.",
                299.00,
                46,
                "Oficina",
                "https://images.unsplash.com/photo-1587145820266-a5951ee6f620?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Set de bandas elásticas",
                "Cinco niveles de resistencia para entrenamiento, movilidad y rehabilitación física.",
                399.00,
                37,
                "Deportes y fitness",
                "https://images.unsplash.com/photo-1598289431512-b97b0917affc?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Balón de fútbol para entrenamiento",
                "Balón resistente con buen control y desempeño para práctica recreativa o deportiva.",
                549.00,
                33,
                "Deportes y fitness",
                "https://images.unsplash.com/photo-1614632537190-23e4146777db?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Botella térmica deportiva de 1 litro",
                "Conserva bebidas frías o calientes y cuenta con tapa segura para llevar a cualquier lugar.",
                449.00,
                41,
                "Deportes y fitness",
                "https://images.unsplash.com/photo-1602143407151-7111542de6e8?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Plancha cerámica para cabello",
                "Placas de calentamiento uniforme, temperatura ajustable y acabado suave.",
                799.00,
                28,
                "Belleza y cuidado personal",
                "https://images.unsplash.com/photo-1522337660859-02fbefca4702?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Espejo LED para maquillaje",
                "Iluminación regulable y base estable para rutinas de maquillaje y cuidado facial.",
                899.00,
                24,
                "Belleza y cuidado personal",
                "https://images.unsplash.com/photo-1526045478516-99145907023c?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Perfume unisex cítrico de 100 ml",
                "Fragancia fresca con notas cítricas y amaderadas para uso cotidiano.",
                1199.00,
                20,
                "Belleza y cuidado personal",
                "https://images.unsplash.com/photo-1541643600914-78b084683601?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Cepillo facial sónico",
                "Limpieza suave con distintas intensidades y diseño resistente al agua.",
                699.00,
                30,
                "Belleza y cuidado personal",
                "https://images.unsplash.com/photo-1556229010-6c3f2c9ca5f8?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Consola portátil retro",
                "Consola compacta con pantalla a color, controles integrados y colección de juegos clásicos.",
                1899.00,
                24,
                "Electrónicos",
                "https://images.pexels.com/photos/3945658/pexels-photo-3945658.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Cargador inalámbrico 3 en 1",
                "Base de carga rápida para teléfono, reloj y audífonos con protección contra sobrecalentamiento.",
                799.00,
                38,
                "Electrónicos",
                "https://images.pexels.com/photos/4526407/pexels-photo-4526407.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Drone compacto con cámara HD",
                "Vuelo estable, cámara de alta definición y control remoto para fotografía aérea recreativa.",
                3299.00,
                13,
                "Electrónicos",
                "https://images.pexels.com/photos/442587/pexels-photo-442587.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Lector electrónico de 6 pulgadas",
                "Pantalla antirreflejo, luz ajustable y almacenamiento amplio para llevar tu biblioteca contigo.",
                2799.00,
                18,
                "Electrónicos",
                "https://images.pexels.com/photos/5082568/pexels-photo-5082568.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Proyector portátil Full HD",
                "Proyección nítida, conexión HDMI y diseño ligero para entretenimiento en cualquier espacio.",
                4599.00,
                12,
                "Electrónicos",
                "https://images.pexels.com/photos/7991378/pexels-photo-7991378.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Robot aspirador inteligente",
                "Limpieza automática con sensores anticaída, programación diaria y retorno a la base de carga.",
                4999.00,
                11,
                "Hogar",
                "https://images.pexels.com/photos/8566472/pexels-photo-8566472.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Purificador de aire con filtro HEPA",
                "Filtración de partículas, modo silencioso y control de velocidad para habitaciones medianas.",
                2699.00,
                16,
                "Hogar",
                "https://images.pexels.com/photos/4792728/pexels-photo-4792728.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Juego de toallas de algodón",
                "Set absorbente y suave con piezas para baño, manos y rostro en colores neutros.",
                649.00,
                36,
                "Hogar",
                "https://images.pexels.com/photos/421037/pexels-photo-421037.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Organizador modular para clóset",
                "Módulos apilables con compartimentos para ropa, accesorios y calzado.",
                1199.00,
                22,
                "Hogar",
                "https://images.pexels.com/photos/271795/pexels-photo-271795.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Difusor ultrasónico de aromas",
                "Nebulización silenciosa, iluminación ambiental y apagado automático.",
                549.00,
                31,
                "Hogar",
                "https://images.pexels.com/photos/965989/pexels-photo-965989.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Marcadores de punta dual 24 colores",
                "Tinta de secado rápido con punta fina y biselada para ilustración y lettering.",
                459.00,
                47,
                "Papelería",
                "https://images.unsplash.com/photo-1513364776144-60967b0f800f?auto=format&fit=crop&w=900&q=80"
            ),
            new ProductoMuestra(
                "Carpeta organizadora expandible",
                "Doce divisiones, etiquetas y cierre seguro para documentos escolares o administrativos.",
                229.00,
                58,
                "Papelería",
                "https://images.pexels.com/photos/733857/pexels-photo-733857.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Pluma estilográfica recargable",
                "Cuerpo metálico, escritura fluida y convertidor de tinta reutilizable.",
                389.00,
                43,
                "Papelería",
                "https://images.pexels.com/photos/261949/pexels-photo-261949.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Kit de notas adhesivas",
                "Notas de distintos tamaños y colores para organizar pendientes, libros y proyectos.",
                169.00,
                70,
                "Papelería",
                "https://images.pexels.com/photos/590493/pexels-photo-590493.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Papel fotográfico brillante 100 hojas",
                "Papel de alta resolución compatible con impresoras de inyección de tinta.",
                349.00,
                39,
                "Papelería",
                "https://images.pexels.com/photos/4348404/pexels-photo-4348404.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Unidad SSD externa de 1 TB",
                "Almacenamiento portátil de alta velocidad con conexión USB-C y carcasa resistente.",
                2199.00,
                27,
                "Computación",
                "https://images.pexels.com/photos/2582937/pexels-photo-2582937.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Hub USB-C 7 en 1",
                "Expande tu equipo con HDMI, USB, lector de tarjetas y carga de alta potencia.",
                899.00,
                34,
                "Computación",
                "https://images.pexels.com/photos/4219861/pexels-photo-4219861.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Soporte de aluminio para laptop",
                "Base ventilada con altura ergonómica y estructura plegable antideslizante.",
                699.00,
                41,
                "Computación",
                "https://images.pexels.com/photos/18105/pexels-photo.jpg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Router Wi-Fi 6 de doble banda",
                "Cobertura estable, alta velocidad y administración sencilla para múltiples dispositivos.",
                1699.00,
                21,
                "Computación",
                "https://images.pexels.com/photos/4219883/pexels-photo-4219883.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Impresora multifuncional Wi-Fi",
                "Imprime, copia y escanea de forma inalámbrica con sistema de tinta de alto rendimiento.",
                3899.00,
                14,
                "Computación",
                "https://images.pexels.com/photos/4792285/pexels-photo-4792285.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Barra de sonido 2.1 canales",
                "Audio claro con graves profundos, conexión Bluetooth y entrada óptica.",
                2799.00,
                19,
                "Audio y video",
                "https://images.pexels.com/photos/157534/pexels-photo-157534.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Audífonos gamer con micrófono",
                "Sonido envolvente, micrófono flexible y almohadillas cómodas para sesiones prolongadas.",
                1099.00,
                29,
                "Audio y video",
                "https://images.pexels.com/photos/3945657/pexels-photo-3945657.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Reproductor multimedia 4K",
                "Convierte cualquier pantalla en centro de entretenimiento con aplicaciones y control por voz.",
                1299.00,
                26,
                "Audio y video",
                "https://images.pexels.com/photos/4790267/pexels-photo-4790267.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Radio portátil con Bluetooth",
                "Sintonizador digital, batería recargable y reproducción inalámbrica en formato compacto.",
                749.00,
                33,
                "Audio y video",
                "https://images.pexels.com/photos/1420003/pexels-photo-1420003.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Soporte articulado para TV",
                "Montaje reforzado con inclinación y giro ajustables para pantallas de hasta 65 pulgadas.",
                949.00,
                25,
                "Audio y video",
                "https://images.pexels.com/photos/6976094/pexels-photo-6976094.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Freidora de aire de 5 litros",
                "Cocción con poco aceite, controles digitales y programas automáticos para recetas cotidianas.",
                2299.00,
                20,
                "Cocina",
                "https://images.pexels.com/photos/1117862/pexels-photo-1117862.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Horno eléctrico de 20 litros",
                "Temperatura regulable, temporizador y funciones para hornear, tostar y gratinar.",
                1899.00,
                17,
                "Cocina",
                "https://images.pexels.com/photos/7936721/pexels-photo-7936721.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Batería de cocina de 10 piezas",
                "Ollas y sartenes con recubrimiento antiadherente, tapas de vidrio y mangos ergonómicos.",
                2599.00,
                15,
                "Cocina",
                "https://images.pexels.com/photos/4226869/pexels-photo-4226869.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Tostador de dos rebanadas",
                "Niveles de dorado ajustables, función de descongelado y bandeja recolectora.",
                699.00,
                32,
                "Cocina",
                "https://images.pexels.com/photos/7936731/pexels-photo-7936731.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Termo dispensador de 3 litros",
                "Conserva la temperatura y sirve bebidas fácilmente con sistema de presión.",
                849.00,
                28,
                "Cocina",
                "https://images.pexels.com/photos/585750/pexels-photo-585750.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Pizarrón magnético de 90 x 60 cm",
                "Superficie borrable, marco ligero y accesorios para reuniones, clases y planeación.",
                899.00,
                24,
                "Oficina",
                "https://images.pexels.com/photos/1181533/pexels-photo-1181533.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Reposapiés ergonómico ajustable",
                "Plataforma inclinable con textura antideslizante para mejorar la postura al trabajar.",
                649.00,
                35,
                "Oficina",
                "https://images.pexels.com/photos/1957478/pexels-photo-1957478.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Destructora de papel compacta",
                "Corte cruzado, depósito removible y protección contra sobrecarga.",
                1499.00,
                18,
                "Oficina",
                "https://images.pexels.com/photos/442150/pexels-photo-442150.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Set de organizadores de escritorio",
                "Bandejas y compartimentos coordinados para documentos, lápices y accesorios.",
                499.00,
                42,
                "Oficina",
                "https://images.pexels.com/photos/590016/pexels-photo-590016.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Mesa plegable para home office",
                "Superficie resistente y estructura compacta que se guarda fácilmente.",
                1799.00,
                16,
                "Oficina",
                "https://images.pexels.com/photos/1957477/pexels-photo-1957477.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Bicicleta fija magnética",
                "Resistencia ajustable, monitor de actividad y asiento regulable para entrenamiento en casa.",
                5499.00,
                9,
                "Deportes y fitness",
                "https://images.pexels.com/photos/4162449/pexels-photo-4162449.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Cuerda para saltar con contador",
                "Longitud ajustable, mangos antideslizantes y registro digital de saltos.",
                349.00,
                46,
                "Deportes y fitness",
                "https://images.pexels.com/photos/6339482/pexels-photo-6339482.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Rodillo para masaje muscular",
                "Textura de alta densidad para recuperación, movilidad y liberación miofascial.",
                499.00,
                37,
                "Deportes y fitness",
                "https://images.pexels.com/photos/4662356/pexels-photo-4662356.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Guantes para entrenamiento",
                "Palma acolchada, tejido transpirable y ajuste seguro para gimnasio o ciclismo.",
                399.00,
                40,
                "Deportes y fitness",
                "https://images.pexels.com/photos/260447/pexels-photo-260447.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Banco ajustable para ejercicio",
                "Respaldo con varias posiciones y estructura reforzada para rutinas de fuerza.",
                3199.00,
                12,
                "Deportes y fitness",
                "https://images.pexels.com/photos/1552242/pexels-photo-1552242.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Afeitadora eléctrica recargable",
                "Cabezales flexibles, uso en seco o húmedo y batería de larga duración.",
                1299.00,
                27,
                "Belleza y cuidado personal",
                "https://images.pexels.com/photos/3998429/pexels-photo-3998429.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Masajeador facial de cuarzo",
                "Rodillo refrescante para complementar la rutina diaria de cuidado de la piel.",
                329.00,
                49,
                "Belleza y cuidado personal",
                "https://images.pexels.com/photos/3735657/pexels-photo-3735657.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Set de brochas para maquillaje",
                "Colección de brochas suaves con estuche para rostro, ojos y detalles.",
                599.00,
                38,
                "Belleza y cuidado personal",
                "https://images.pexels.com/photos/457701/pexels-photo-457701.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Organizador acrílico para cosméticos",
                "Cajones transparentes y compartimentos superiores para mantener productos visibles y ordenados.",
                749.00,
                30,
                "Belleza y cuidado personal",
                "https://images.pexels.com/photos/3373739/pexels-photo-3373739.jpeg?auto=compress&cs=tinysrgb&w=900"
            ),
            new ProductoMuestra(
                "Báscula corporal inteligente",
                "Medición de peso y composición corporal con sincronización mediante aplicación móvil.",
                899.00,
                23,
                "Belleza y cuidado personal",
                "https://images.pexels.com/photos/5340280/pexels-photo-5340280.jpeg?auto=compress&cs=tinysrgb&w=900"
            )
        );
    }

    private boolean imagenNecesitaCorreccion(String imagenUrl) {
        return imagenUrl.contains("/photos/618753/")
            || imagenUrl.contains("/photos/159644/");
    }

    private String clave(String valor) {
        return Normalizer.normalize(valor, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .trim()
            .toLowerCase(Locale.ROOT);
    }

    private record ProductoMuestra(
        String nombre,
        String descripcion,
        Double precio,
        Integer stock,
        String categoria,
        String imagenUrl
    ) {
    }
}
