#ifndef DEFS_STRUCTS_ENUM_H
#define DEFS_STRUCTS_ENUM_H

#include <stdbool.h>

typedef enum{
    CTE_LITERARIA,
    CTE_NUMERICA
}tipo_de_constante;

typedef struct{
    tipo_de_constante tipo;
    int valor_numerico;
    char valor_lit [1000];
}constante_final;

typedef enum{
    PALABRA_CLAVE,
    INTEGER,
    STRINGER
}tipo_entrada_tabla;
    
typedef struct Nodo{
    char clave[50];
    tipo_entrada_tabla tipo;
    bool es_cte;
    int dato_entero;
    char dato_lit[255];
    struct Nodo* sgte;
        
}nodo_lista;

typedef struct Nodo_expresiones{
    constante_final expresion;
    struct Nodo_expresiones* sgte;
}nodo_lista_expresiones;

typedef struct Nodo_Ids{
    char id [33];
    struct Nodo_Ids* sgte;
}nodo_lista_ids;
    
typedef struct{
    nodo_lista* head;
}lista_enlazada;

typedef struct{
    nodo_lista_expresiones* head;
}lista_enlazada_expresiones;

typedef struct{
    nodo_lista_ids* head;
}lista_enlazada_ids;

#endif