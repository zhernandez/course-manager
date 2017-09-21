package course.manager

class Course {

    static hasMany = [lessons: Lesson, students: Student]
    String name
    //Texto de descripcion del curso
    String description
    //Texto de Curso Finalizado
    String banner
    //Texto de bienvenida
    String welcome
    //Texto del boton de teoria
    String theoryButton
    //URL de la foto de pagina de curso, url
    String coursePhoto
    //URL de la foto de pagina de lecciones, url
    String lessonPhoto
    //Texto de pagina de teoria
    String theory
    //Titulo de pagina de teoria
    String theoryTitle
    //Texto de pagina de informacion, no está en Firebase
    String info
    //URL para uso en parametros
    String url


    static constraints = {

        name blank: false
        description blank: false
        banner blank: false
        welcome blank: false
        theoryButton blank: false
        coursePhoto blank: false
        lessonPhoto blank: false
        theory blank: false
        theoryTitle blank: false
        info blank: false
        url blank: false

    }

    //Agregamos este mapping para que no la haga de a pedo H2 e incluso MySQL
    static mapping = {
        welcome sqlType: "text"
        description sqlType: "text"
        theory sqlType: "text"
    }
}
