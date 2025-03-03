%{
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include "defs_structs_enum.h"
#include "codigo_bison.tab.h"


void yyerror(const char*);
int yyparse(void);
void errorExcesoCaracteres(void);
void errorLexico(void);
bool salidaEstandar;



%}

%option yylineno
%option noyywrap


%%
 //EL option yylineno permite contar las lineas para indicar en cual ocurrio el error

 //Se definen los tokens

[ \t\n]+                    ; //Se ignoran los espacios en blanco y saltos de linea
"inicio"                    return INICIO;
"fin"                       return FIN;
"escribir"                  return ESCRIBIR;
"leer"                      return LEER;
"int"                       return INT;
"string"                    return STRING;
"const"                     return CONST;
[a-zA-Z_][a-zA-Z0-9_]*      {
                                if(strlen(yytext)>32){
                                    errorExcesoCaracteres(); //Los identificadores no pueden superar los 32 caracteres segun el lenguaje micro
                                }
                                strcpy(yylval.id_val, yytext);
                                return IDENTIFICADOR;} //Se guardan los identificadotes
\"[^\"]*\"                   {
                                
                                strcpy(yylval.cte_lit_val, yytext + 1); //Elimino la primera comilla
                                yylval.cte_lit_val[strlen(yylval.cte_lit_val)- 1 ] = '\0' ; //Elimino la ultima comilla
                                return CONSTANTE_LIT;}
[0-9][0-9]*                 {yylval.cte_num_val = atoi(yytext); return CONSTANTE_NUM;}
":="                        return OP_ASIG;
"+"                         return OP_SUMA;
"-"                         return OP_RESTA;
"("                         return P_IZQUIERDO;
")"                         return P_DERECHO;
";"                         return PUNTOYCOMA;
","                         return COMA;
"//".*                      ; //Ignorar los comentarios
.                           {errorLexico();} //Si ninguna regla aplico hasta este punto, hubo un error lexico
%%

void errorExcesoCaracteres(){
    printf("\nError lexico en linea : %d. Los identificadores tienen un maximo de 32 caracteres. \n", yylineno);
    getchar();
    exit(0); //Fin del programa
}

void errorLexico(){
    printf("\nOcurrio un error lexico en la linea %d. \n", yylineno);
    getchar();
    exit(0);
}

void yyerror(const char* str) {
    printf("%s. En linea: %d. \n", str, yylineno);
}

int main(){
    salidaEstandar = false;
    int* respuesta = malloc (sizeof(int));
    printf("Bienvenido al Interprete del lenguaje Micro. Si desea realizar una entrada estandar, ingrese 1. Si desea analizar un archivo, ingrese 2. Si quiere salir, ingrese otro numero.\n");
    scanf("%d", respuesta);
    scanf("%*c"); //Leer el salto de linea que ignoro scanf y descartarlo

    switch (*respuesta){
        case 1:
            ;
            salidaEstandar = true;
            printf("Ustedes escogio entrada estandar, porfavor ingrese la entrada (CTRL + C para finalizar)\n");
            yyparse();
            break;
        case 2:
            ;
            char nombre_arch [50 + 1];
            printf("Usted escogio entrada por archivo, por favor ingrese el nombre del archivo (Recuerde que el archivo debe estar en la misma carpeta que el Interprete y debe escribir su extension; por ejemplo \"Archivo.txt\" \n");
            fgets(nombre_arch, 51, stdin);
            nombre_arch[strlen(nombre_arch) - 1] = '\0';
            fflush(stdin);

            FILE* archivo;

            archivo = fopen(nombre_arch, "r");

            while(archivo == NULL){
                printf("Error al abrir el archivo. Asegurese de escribir bien el nombre nuevamente. \n");
                fgets(nombre_arch, 51, stdin);
                nombre_arch[strlen(nombre_arch) - 1] = '\0';
                fflush(stdin);
                archivo = fopen(nombre_arch, "r");

            }

            yyin = archivo;
            yyparse();
            fclose(archivo);
            break;

    }

    getchar(); //Para que la consola en windows no se cierre automaticamente

    return 1;
    
}