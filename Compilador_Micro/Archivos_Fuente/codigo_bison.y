%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <ctype.h>
#include "defs_structs_enum.h"


extern void yyerror(const char*);
extern int yylex(void);
extern int yylineno;
extern bool salidaEstandar;




lista_enlazada* tabla_de_simbolos;
lista_enlazada_ids* lista_identificadores;
lista_enlazada_expresiones* lista_de_expresiones;

void errorPalabraClave(char*);
void errorIdNoDeclarado(char*);
void errorModificarCte(char*);
void errorTipoEquivocado(char*, char*);
void errorCaracteres();
void errorReDeclaracion(char*);
void errorExpresionErronea(tipo_de_constante);
void errorOperacionString();
void agregar_elemento_a_lista(lista_enlazada* , nodo_lista* );
void agregar_palabra_clave(lista_enlazada* , char* );
void iniciar_tabla_de_simbolos(lista_enlazada* );
nodo_lista* buscar_elemento(lista_enlazada* , char*);
void eliminar_tabla(lista_enlazada*);
void eliminar_lista_ids(lista_enlazada_ids*);
void eliminar_lista_expresiones(lista_enlazada_expresiones*);
void agregar_expresion(lista_enlazada_expresiones*, constante_final);
void agregar_id(lista_enlazada_ids*, char*, lista_enlazada*);
void modificar_dato_entero(lista_enlazada*, char*, int);
void modificar_dato_lit(lista_enlazada*, char*, char*);
void leer_identificadores(lista_enlazada_ids*, lista_enlazada*);
void escribir_expresiones(lista_enlazada_expresiones*);
void agregar_int_a_tabla(lista_enlazada* , char*, int, bool);
void agregar_string_a_tabla(lista_enlazada*, char*, char*, bool);
bool existe_el_id(lista_enlazada*, char*);
bool es_id_valido(lista_enlazada*, char*);
bool es_una_constante(lista_enlazada*, char*);
tipo_entrada_tabla es_int_o_string(lista_enlazada*, char*);
void fin_de_analisis(lista_enlazada*);
bool es_numero(char*);
%}

%define parse.error detailed 

%union {
    int     cte_num_val;
    char   id_val[33];
    char cte_lit_val [1000];
    constante_final cte_fin;
}

%token <cte_num_val> INICIO FIN ESCRIBIR LEER INT STRING CONST CONSTANTE_NUM OP_ASIG OP_SUMA OP_RESTA P_IZQUIERDO P_DERECHO PUNTOYCOMA COMA
%token <id_val> IDENTIFICADOR
%token <cte_lit_val> CONSTANTE_LIT

%type <cte_fin> primaria expresion;

%left OP_SUMA OP_RESTA

%start programa

%%

programa:                       INICIO{tabla_de_simbolos = malloc(sizeof(lista_enlazada)); iniciar_tabla_de_simbolos(tabla_de_simbolos);} lista_sentencias FIN{if(salidaEstandar) fin_de_analisis(tabla_de_simbolos); } YYEOF{fin_de_analisis(tabla_de_simbolos);}|
                                INICIO{tabla_de_simbolos = malloc(sizeof(lista_enlazada)); iniciar_tabla_de_simbolos(tabla_de_simbolos);} FIN{ if(salidaEstandar) fin_de_analisis(tabla_de_simbolos);} YYEOF{fin_de_analisis(tabla_de_simbolos);};

lista_sentencias:               lista_sentencias sentencia | sentencia;

sentencia:                      LEER{lista_identificadores = malloc(sizeof(lista_enlazada_ids)); lista_identificadores -> head = NULL;} P_IZQUIERDO listaIds P_DERECHO PUNTOYCOMA{
                                    leer_identificadores(lista_identificadores, tabla_de_simbolos);
                                    eliminar_lista_ids(lista_identificadores);
                                } |
                                ESCRIBIR{lista_de_expresiones = malloc(sizeof(lista_enlazada_expresiones)); lista_de_expresiones -> head = NULL;} P_IZQUIERDO listaExpresiones P_DERECHO PUNTOYCOMA{
                                    escribir_expresiones(lista_de_expresiones);
                                    eliminar_lista_expresiones(lista_de_expresiones);
                                } |
                                INT IDENTIFICADOR PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $2)){
                                        agregar_int_a_tabla(tabla_de_simbolos, $2, 0, false);
                                    }
                                } |
                                IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA{
                                    if(existe_el_id(tabla_de_simbolos, $1)){
                                        if(!es_una_constante(tabla_de_simbolos, $1)){
                                            switch(es_int_o_string(tabla_de_simbolos, $1)){
                                                case INTEGER:
                                                    if(($3).tipo == CTE_NUMERICA){
                                                        modificar_dato_entero(tabla_de_simbolos, $1, ($3).valor_numerico);
                                                    }
                                                    else{
                                                        errorExpresionErronea(CTE_LITERARIA);
                                                    }
                                                    break;
                                                case STRINGER:
                                                    if(($3).tipo == CTE_LITERARIA){
                                                        if(strlen(($3).valor_lit) < 255){
                                                            modificar_dato_lit(tabla_de_simbolos, $1, ($3).valor_lit);
                                                        }
                                                        else{
                                                            errorCaracteres();
                                                        }
                                                        
                                                    }
                                                    else{
                                                        errorExpresionErronea(CTE_NUMERICA);
                                                    }
                                                    break;
                                            }
                                            
                                        }
                                        else{
                                            errorModificarCte($1);
                                        }
                                    }
                                    else{
                                        errorIdNoDeclarado($1);
                                    }
                                } |
                                INT IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $2)){
                                        if(($4).tipo == CTE_NUMERICA){
                                            agregar_int_a_tabla(tabla_de_simbolos, $2, ($4).valor_numerico, false);
                                        }
                                        else{
                                            errorExpresionErronea(CTE_LITERARIA);
                                        }
                                    }
                                } |
                                CONST INT IDENTIFICADOR PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $3)){
                                        agregar_int_a_tabla(tabla_de_simbolos, $3, 0, true);
                                    }
                                } |
                                CONST INT IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $3)){
                                        if(($5).tipo == CTE_NUMERICA){
                                            agregar_int_a_tabla(tabla_de_simbolos, $3, ($5).valor_numerico, true);
                                        }
                                        else{
                                            errorExpresionErronea(CTE_LITERARIA);
                                        }
                                    }
                                    
                                } |
                                STRING IDENTIFICADOR PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $2)){
                                        agregar_string_a_tabla(tabla_de_simbolos, $2, "", false);
                                    }
                                } |
                                STRING IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $2)){
                                        if(($4).tipo == CTE_LITERARIA){
                                            if(strlen(($4).valor_lit)<255){
                                                agregar_string_a_tabla(tabla_de_simbolos, $2, ($4).valor_lit, false);
                                            }
                                            else{
                                                errorCaracteres();
                                            }
                                            
                                        }
                                        else{
                                            errorExpresionErronea(CTE_NUMERICA);
                                        }
                                    }
                                } |
                                CONST STRING IDENTIFICADOR PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $3)){
                                        agregar_string_a_tabla(tabla_de_simbolos, $3, "", true);
                                    }
                                } |
                                CONST STRING IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA{
                                    if(es_id_valido(tabla_de_simbolos, $3)){
                                        if(($5).tipo == CTE_LITERARIA){
                                            if(strlen(($5).valor_lit)<255){
                                                agregar_string_a_tabla(tabla_de_simbolos, $3, ($5).valor_lit, true);
                                            }
                                            else{
                                                errorCaracteres();
                                            }
                                            
                                        }
                                        else{
                                            errorExpresionErronea(CTE_NUMERICA);
                                        }
                                    }
                                } ;

listaIds:                       IDENTIFICADOR{agregar_id(lista_identificadores,$1, tabla_de_simbolos);} COMA listaIds{} | IDENTIFICADOR{agregar_id(lista_identificadores,$1, tabla_de_simbolos);};

listaExpresiones:               expresion{agregar_expresion(lista_de_expresiones, $1);} COMA listaExpresiones{} | expresion{agregar_expresion(lista_de_expresiones, $1);} ;

expresion:                      expresion OP_SUMA primaria {
                                        if(($1).tipo == CTE_LITERARIA || ($3).tipo == CTE_LITERARIA){
                                            errorOperacionString();
                                        }
                                        else{
                                            $$.tipo = CTE_NUMERICA;
                                            $$.valor_numerico = ($1).valor_numerico + ($3).valor_numerico;
                                        }
                                            } |
                                expresion OP_RESTA primaria {
                                        if(($1).tipo == CTE_LITERARIA || ($3).tipo == CTE_LITERARIA){
                                            errorOperacionString();
                                        }
                                        else{
                                            $$.tipo = CTE_NUMERICA;
                                            $$.valor_numerico = ($1).valor_numerico - ($3).valor_numerico;
                                        }
                                            }|
                                primaria{
                                    $$ = $1;
                                };

primaria:                       IDENTIFICADOR{
                                            nodo_lista* buscador = buscar_elemento(tabla_de_simbolos, $1);
                                            if(buscador == NULL){
                                                errorIdNoDeclarado($1);
                                            }
                                            else{
                                                switch(buscador -> tipo){
                                                    case PALABRA_CLAVE:
                                                        errorPalabraClave($1);
                                                        break;
                                                    case STRINGER:
                                                        $$.tipo = CTE_LITERARIA;
                                                        strcpy($$.valor_lit, buscador ->dato_lit);
                                                        break;
                                                    case INTEGER:
                                                        $$.tipo = CTE_NUMERICA;
                                                        $$.valor_numerico = buscador ->dato_entero;
                                                }
                                            }
                                                    } |
                                CONSTANTE_LIT {$$.tipo = CTE_LITERARIA;
                                                strcpy($$.valor_lit, $1);}|
                                CONSTANTE_NUM {$$.tipo = CTE_NUMERICA;
                                                $$.valor_numerico = $1;}|
                                OP_RESTA CONSTANTE_NUM {$$.tipo = CTE_NUMERICA;
                                                $$.valor_numerico = $2 * (-1);} |
                                P_IZQUIERDO expresion P_DERECHO {$$ = $2;}|

                                OP_RESTA P_IZQUIERDO expresion P_DERECHO {
                                    if(($3).tipo == CTE_NUMERICA){
                                        $$.tipo = CTE_NUMERICA;
                                        $$.valor_numerico = (-1) * ($3).valor_numerico;
                                    }
                                    else{
                                        errorExpresionErronea(CTE_LITERARIA);
                                    }
                                };
                                

%%

void errorPalabraClave(char* id){
    printf("\nError semantico en linea: %d. %s es una palabra reservada. \n", yylineno, id);
    getchar();
    exit(0);
}

void errorIdNoDeclarado(char* id){
    printf("\nError semantico en linea: %d. %s no esta declarado. \n", yylineno, id);
    getchar();
    exit(0);
}

void errorModificarCte(char* id){
    printf("\nError semantico en linea: %d. Se intento modificar el valor de la constante %s. \n", yylineno, id);
    getchar();
    exit(0);
}

void errorTipoEquivocado(char* tipo_esperado, char* tipo_recibido){
    printf("\nError semantico en linea: %d. Se esperaba un %s pero se recibio un %s. \n", yylineno, tipo_esperado, tipo_recibido);
    getchar();
    exit(0);
}

void errorCaracteres(){
    printf("\nError semantico en linea : %d. Los string tienen un maximo de 254 caracteres. \n", yylineno);
    getchar();
    exit(0); //Fin del programa
}

void errorReDeclaracion(char* id){
    printf("\nError semantico en linea: %d. Se esta re declarando el identificador: %s. \n", yylineno, id);
    getchar();
    exit(0);
}

void errorExpresionErronea(tipo_de_constante tipo_inesperado){
    switch(tipo_inesperado){
        case CTE_NUMERICA:
            printf("\nError semantico en linea: %d. Se esperaba que la expresion fuera de tipo Constante Literaria, pero se recibio Constante Numerica. \n", yylineno);
            break;
        case CTE_LITERARIA:
            printf("\nError semantico en linea: %d. Se esperaba que la expresion fuera de tipo Constante Numerica, pero se recibio Constante Literaria. \n", yylineno);
            break;
        
    }
    getchar();
    exit(0);
}

void errorOperacionString(){
    printf("\nError semantico en linea: %d. Se intento realizar una operacion matematica con un string \n", yylineno);
    getchar();
    exit(0);
}

void agregar_elemento_a_lista(lista_enlazada* lista, nodo_lista* nodo){
    nodo->sgte = lista->head;
    lista->head = nodo;
}

void agregar_palabra_clave(lista_enlazada* tabla, char* palabra){
    nodo_lista* nuevo_nodo = malloc(sizeof(nodo_lista));
    strcpy(nuevo_nodo->clave, palabra);
    nuevo_nodo->tipo = PALABRA_CLAVE;
    nuevo_nodo->es_cte = false;
    nuevo_nodo->sgte = NULL;
    agregar_elemento_a_lista(tabla, nuevo_nodo);
}

void iniciar_tabla_de_simbolos(lista_enlazada* tabla_de_simbolos){
    tabla_de_simbolos->head = NULL;
    agregar_palabra_clave(tabla_de_simbolos, "inicio");
    agregar_palabra_clave(tabla_de_simbolos, "fin");
    agregar_palabra_clave(tabla_de_simbolos, "leer");
    agregar_palabra_clave(tabla_de_simbolos, "escribir");
    agregar_palabra_clave(tabla_de_simbolos, "int");
    agregar_palabra_clave(tabla_de_simbolos, "string");
    agregar_palabra_clave(tabla_de_simbolos, "const");
}

nodo_lista* buscar_elemento(lista_enlazada* tabla, char* clave){
    nodo_lista* buscador = tabla->head;
    
    while(buscador != NULL){
        if(strcmp(buscador->clave, clave)==0){
            return buscador;
        }
        
        buscador = buscador ->sgte;
    }
    
    return NULL;
}

void eliminar_tabla(lista_enlazada* tabla){
    nodo_lista* eliminador = tabla->head;
    while(eliminador != NULL){
        nodo_lista* a_borrar = eliminador;
        eliminador = eliminador->sgte;
        free(a_borrar);
    }
    free(tabla);
}

void eliminar_lista_ids(lista_enlazada_ids* lista){
    nodo_lista_ids* eliminador = lista->head;
    while(eliminador != NULL){
        nodo_lista_ids* a_borrar = eliminador;
        eliminador = eliminador->sgte;
        free(a_borrar);
    }
    free(lista);
}

void eliminar_lista_expresiones(lista_enlazada_expresiones* lista){
    nodo_lista_expresiones* eliminador = lista->head;
    while(eliminador != NULL){
        nodo_lista_expresiones* a_borrar = eliminador;
        eliminador = eliminador->sgte;
        free(a_borrar);
    }
    free(lista);
}

void agregar_expresion(lista_enlazada_expresiones* lista, constante_final exp){
    nodo_lista_expresiones* nuevo_nodo = malloc(sizeof(nodo_lista_expresiones));
    nuevo_nodo->expresion = exp;
    nuevo_nodo -> sgte = NULL;

    nodo_lista_expresiones* avanzador = lista -> head;
    if(avanzador == NULL){
        lista -> head = nuevo_nodo;
    }
    else{
        while(avanzador->sgte != NULL){
            avanzador = avanzador -> sgte;
        }
        avanzador -> sgte = nuevo_nodo;
    }
    
}

void agregar_id(lista_enlazada_ids* lista, char* id_a_cargar, lista_enlazada* tabla_de_simbolos){
    
    nodo_lista* buscador = buscar_elemento(tabla_de_simbolos, id_a_cargar);
    if(buscador == NULL){
        errorIdNoDeclarado(id_a_cargar);
    }
    else if(buscador -> tipo == PALABRA_CLAVE){
        errorPalabraClave(id_a_cargar);              
    }
    else if(buscador ->es_cte){
        errorModificarCte(id_a_cargar);
    }
    else{
        nodo_lista_ids* nuevo_nodo = malloc(sizeof(nodo_lista_ids));
        strcpy(nuevo_nodo ->id, id_a_cargar);
        nuevo_nodo -> sgte = NULL;
        nodo_lista_ids* avanzador = lista -> head;
        if(avanzador == NULL){
            lista -> head = nuevo_nodo;
        }
        else{
            while(avanzador->sgte != NULL){
                avanzador = avanzador -> sgte;
            }
            avanzador -> sgte = nuevo_nodo;
        }
    }
    
}

void modificar_dato_entero(lista_enlazada* tabla, char* id, int valor){
    nodo_lista* dato_a_mod = buscar_elemento(tabla, id);

    dato_a_mod -> dato_entero = valor;
}

void modificar_dato_lit(lista_enlazada* tabla, char* id, char* valor){
    nodo_lista* dato_a_mod = buscar_elemento(tabla, id);

    strcpy(dato_a_mod -> dato_lit, valor);
}

void leer_identificadores(lista_enlazada_ids* lista, lista_enlazada* tabla){
    nodo_lista_ids* avanzador = lista -> head;
    nodo_lista* dato_a_mod = NULL;

    while(avanzador != NULL){
        dato_a_mod = buscar_elemento(tabla, avanzador ->id);
        switch(dato_a_mod ->tipo){
            case INTEGER:
                ;
                char respuesta_num [400];
                printf("Ingrese el dato para el id: %s (Debe ser un dato de tipo entero)\n", dato_a_mod -> clave);
                fgets(respuesta_num, 400, stdin);
                fflush(stdin);
                respuesta_num[strlen(respuesta_num) - 1] = '\0';
                if(es_numero(respuesta_num)){
                    dato_a_mod ->dato_entero = atoi(respuesta_num);
                }
                else{
                    
                    errorTipoEquivocado("Entero", "String");
                    
                }

                break;
            case STRINGER:
                ;
                char respuesta_lit [400];
                printf("Ingrese el dato para el id: %s (Debe ser un dato de tipo string)\n", dato_a_mod -> clave);
                fgets(respuesta_lit, 400, stdin);
                fflush(stdin);
                respuesta_lit[strlen(respuesta_lit) - 1] = '\0';
                if(strlen(respuesta_lit)>254){
                    errorCaracteres();
                }
                else{
                    strcpy(dato_a_mod -> dato_lit, respuesta_lit);
                }
                
                break;
        }
        avanzador = avanzador -> sgte;
    }
}

void escribir_expresiones(lista_enlazada_expresiones* lista){
    nodo_lista_expresiones* a_escribir = lista->head;
    if(a_escribir != NULL){
        switch(a_escribir->expresion.tipo){
            case CTE_LITERARIA:
                printf("%s\n", a_escribir->expresion.valor_lit);
                break;
            case CTE_NUMERICA:
                printf("%d\n", a_escribir->expresion.valor_numerico);
                break;
        }
        a_escribir = a_escribir ->sgte;
    }
    while(a_escribir != NULL){
        switch(a_escribir->expresion.tipo){
            case CTE_LITERARIA:
                printf("%s\n", a_escribir->expresion.valor_lit);
                break;
            case CTE_NUMERICA:
                printf("%d\n", a_escribir->expresion.valor_numerico);
                break;
        }
        a_escribir = a_escribir ->sgte;
    }
}

void agregar_int_a_tabla(lista_enlazada* tabla_de_simbolos, char* id, int valor, bool es_constante){
    nodo_lista* buscador = malloc(sizeof(nodo_lista));
    buscador -> sgte = NULL;
    buscador -> es_cte = es_constante;
    strcpy(buscador -> clave, id);
    buscador -> tipo = INTEGER;
    buscador -> dato_entero = valor;
    agregar_elemento_a_lista(tabla_de_simbolos, buscador);
}

void agregar_string_a_tabla(lista_enlazada* tabla_de_simbolos, char* id, char* valor_lit, bool es_constante){
    nodo_lista* buscador = malloc(sizeof(nodo_lista));
    buscador -> sgte = NULL;
    buscador -> es_cte = es_constante;
    strcpy(buscador -> clave, id);
    buscador -> tipo = STRINGER;
    strcpy(buscador -> dato_lit, valor_lit);
    agregar_elemento_a_lista(tabla_de_simbolos, buscador);
}

bool existe_el_id(lista_enlazada* tabla, char* id){
    nodo_lista* buscador = buscar_elemento(tabla, id);

    return buscador != NULL;
}

bool es_id_valido(lista_enlazada* tabla_de_simbolos, char* id){
    
    if(!existe_el_id(tabla_de_simbolos, id)){
        return true;
    }
    else{
        errorReDeclaracion(id);
    }
}

bool es_una_constante(lista_enlazada* tabla, char* id){
    nodo_lista* buscador = buscar_elemento(tabla_de_simbolos, id);
    return buscador -> es_cte;
}

tipo_entrada_tabla es_int_o_string(lista_enlazada* tabla, char* id){
    nodo_lista* buscador = buscar_elemento(tabla, id);

    return buscador -> tipo;
}

void fin_de_analisis(lista_enlazada* tabla){
    eliminar_tabla(tabla);
    printf("El codigo se ha compilado sin problemas\n");
    getchar();
    exit(0);
}

bool es_numero(char* cadena){
    
    for(int i = 0; cadena[i] != '\0'; i++){
        if(!isdigit(cadena[i])){
            return false;
        }
    }

    return true;
}