/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison implementation for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* C LALR(1) parser skeleton written by Richard Stallman, by
   simplifying the original so-called "semantic" parser.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

/* All symbols defined below should begin with yy or YY, to avoid
   infringing on user name space.  This should be done even for local
   variables, as they might otherwise be expanded by user macros.
   There are some unavoidable exceptions within include files to
   define necessary library symbols; they are noted "INFRINGES ON
   USER NAME SPACE" below.  */

/* Identify Bison output, and Bison version.  */
#define YYBISON 30802

/* Bison version string.  */
#define YYBISON_VERSION "3.8.2"

/* Skeleton name.  */
#define YYSKELETON_NAME "yacc.c"

/* Pure parsers.  */
#define YYPURE 0

/* Push parsers.  */
#define YYPUSH 0

/* Pull parsers.  */
#define YYPULL 1




/* First part of user prologue.  */
#line 1 "codigo_bison.y"

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

#line 123 "codigo_bison.tab.c"

# ifndef YY_CAST
#  ifdef __cplusplus
#   define YY_CAST(Type, Val) static_cast<Type> (Val)
#   define YY_REINTERPRET_CAST(Type, Val) reinterpret_cast<Type> (Val)
#  else
#   define YY_CAST(Type, Val) ((Type) (Val))
#   define YY_REINTERPRET_CAST(Type, Val) ((Type) (Val))
#  endif
# endif
# ifndef YY_NULLPTR
#  if defined __cplusplus
#   if 201103L <= __cplusplus
#    define YY_NULLPTR nullptr
#   else
#    define YY_NULLPTR 0
#   endif
#  else
#   define YY_NULLPTR ((void*)0)
#  endif
# endif

#include "codigo_bison.tab.h"
/* Symbol kind.  */
enum yysymbol_kind_t
{
  YYSYMBOL_YYEMPTY = -2,
  YYSYMBOL_YYEOF = 0,                      /* "end of file"  */
  YYSYMBOL_YYerror = 1,                    /* error  */
  YYSYMBOL_YYUNDEF = 2,                    /* "invalid token"  */
  YYSYMBOL_INICIO = 3,                     /* INICIO  */
  YYSYMBOL_FIN = 4,                        /* FIN  */
  YYSYMBOL_ESCRIBIR = 5,                   /* ESCRIBIR  */
  YYSYMBOL_LEER = 6,                       /* LEER  */
  YYSYMBOL_INT = 7,                        /* INT  */
  YYSYMBOL_STRING = 8,                     /* STRING  */
  YYSYMBOL_CONST = 9,                      /* CONST  */
  YYSYMBOL_CONSTANTE_NUM = 10,             /* CONSTANTE_NUM  */
  YYSYMBOL_OP_ASIG = 11,                   /* OP_ASIG  */
  YYSYMBOL_OP_SUMA = 12,                   /* OP_SUMA  */
  YYSYMBOL_OP_RESTA = 13,                  /* OP_RESTA  */
  YYSYMBOL_P_IZQUIERDO = 14,               /* P_IZQUIERDO  */
  YYSYMBOL_P_DERECHO = 15,                 /* P_DERECHO  */
  YYSYMBOL_PUNTOYCOMA = 16,                /* PUNTOYCOMA  */
  YYSYMBOL_COMA = 17,                      /* COMA  */
  YYSYMBOL_IDENTIFICADOR = 18,             /* IDENTIFICADOR  */
  YYSYMBOL_CONSTANTE_LIT = 19,             /* CONSTANTE_LIT  */
  YYSYMBOL_YYACCEPT = 20,                  /* $accept  */
  YYSYMBOL_programa = 21,                  /* programa  */
  YYSYMBOL_22_1 = 22,                      /* $@1  */
  YYSYMBOL_23_2 = 23,                      /* $@2  */
  YYSYMBOL_24_3 = 24,                      /* $@3  */
  YYSYMBOL_25_4 = 25,                      /* $@4  */
  YYSYMBOL_lista_sentencias = 26,          /* lista_sentencias  */
  YYSYMBOL_sentencia = 27,                 /* sentencia  */
  YYSYMBOL_28_5 = 28,                      /* $@5  */
  YYSYMBOL_29_6 = 29,                      /* $@6  */
  YYSYMBOL_listaIds = 30,                  /* listaIds  */
  YYSYMBOL_31_7 = 31,                      /* $@7  */
  YYSYMBOL_listaExpresiones = 32,          /* listaExpresiones  */
  YYSYMBOL_33_8 = 33,                      /* $@8  */
  YYSYMBOL_expresion = 34,                 /* expresion  */
  YYSYMBOL_primaria = 35                   /* primaria  */
};
typedef enum yysymbol_kind_t yysymbol_kind_t;




#ifdef short
# undef short
#endif

/* On compilers that do not define __PTRDIFF_MAX__ etc., make sure
   <limits.h> and (if available) <stdint.h> are included
   so that the code can choose integer types of a good width.  */

#ifndef __PTRDIFF_MAX__
# include <limits.h> /* INFRINGES ON USER NAME SPACE */
# if defined __STDC_VERSION__ && 199901 <= __STDC_VERSION__
#  include <stdint.h> /* INFRINGES ON USER NAME SPACE */
#  define YY_STDINT_H
# endif
#endif

/* Narrow types that promote to a signed type and that can represent a
   signed or unsigned integer of at least N bits.  In tables they can
   save space and decrease cache pressure.  Promoting to a signed type
   helps avoid bugs in integer arithmetic.  */

#ifdef __INT_LEAST8_MAX__
typedef __INT_LEAST8_TYPE__ yytype_int8;
#elif defined YY_STDINT_H
typedef int_least8_t yytype_int8;
#else
typedef signed char yytype_int8;
#endif

#ifdef __INT_LEAST16_MAX__
typedef __INT_LEAST16_TYPE__ yytype_int16;
#elif defined YY_STDINT_H
typedef int_least16_t yytype_int16;
#else
typedef short yytype_int16;
#endif

/* Work around bug in HP-UX 11.23, which defines these macros
   incorrectly for preprocessor constants.  This workaround can likely
   be removed in 2023, as HPE has promised support for HP-UX 11.23
   (aka HP-UX 11i v2) only through the end of 2022; see Table 2 of
   <https://h20195.www2.hpe.com/V2/getpdf.aspx/4AA4-7673ENW.pdf>.  */
#ifdef __hpux
# undef UINT_LEAST8_MAX
# undef UINT_LEAST16_MAX
# define UINT_LEAST8_MAX 255
# define UINT_LEAST16_MAX 65535
#endif

#if defined __UINT_LEAST8_MAX__ && __UINT_LEAST8_MAX__ <= __INT_MAX__
typedef __UINT_LEAST8_TYPE__ yytype_uint8;
#elif (!defined __UINT_LEAST8_MAX__ && defined YY_STDINT_H \
       && UINT_LEAST8_MAX <= INT_MAX)
typedef uint_least8_t yytype_uint8;
#elif !defined __UINT_LEAST8_MAX__ && UCHAR_MAX <= INT_MAX
typedef unsigned char yytype_uint8;
#else
typedef short yytype_uint8;
#endif

#if defined __UINT_LEAST16_MAX__ && __UINT_LEAST16_MAX__ <= __INT_MAX__
typedef __UINT_LEAST16_TYPE__ yytype_uint16;
#elif (!defined __UINT_LEAST16_MAX__ && defined YY_STDINT_H \
       && UINT_LEAST16_MAX <= INT_MAX)
typedef uint_least16_t yytype_uint16;
#elif !defined __UINT_LEAST16_MAX__ && USHRT_MAX <= INT_MAX
typedef unsigned short yytype_uint16;
#else
typedef int yytype_uint16;
#endif

#ifndef YYPTRDIFF_T
# if defined __PTRDIFF_TYPE__ && defined __PTRDIFF_MAX__
#  define YYPTRDIFF_T __PTRDIFF_TYPE__
#  define YYPTRDIFF_MAXIMUM __PTRDIFF_MAX__
# elif defined PTRDIFF_MAX
#  ifndef ptrdiff_t
#   include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  endif
#  define YYPTRDIFF_T ptrdiff_t
#  define YYPTRDIFF_MAXIMUM PTRDIFF_MAX
# else
#  define YYPTRDIFF_T long
#  define YYPTRDIFF_MAXIMUM LONG_MAX
# endif
#endif

#ifndef YYSIZE_T
# ifdef __SIZE_TYPE__
#  define YYSIZE_T __SIZE_TYPE__
# elif defined size_t
#  define YYSIZE_T size_t
# elif defined __STDC_VERSION__ && 199901 <= __STDC_VERSION__
#  include <stddef.h> /* INFRINGES ON USER NAME SPACE */
#  define YYSIZE_T size_t
# else
#  define YYSIZE_T unsigned
# endif
#endif

#define YYSIZE_MAXIMUM                                  \
  YY_CAST (YYPTRDIFF_T,                                 \
           (YYPTRDIFF_MAXIMUM < YY_CAST (YYSIZE_T, -1)  \
            ? YYPTRDIFF_MAXIMUM                         \
            : YY_CAST (YYSIZE_T, -1)))

#define YYSIZEOF(X) YY_CAST (YYPTRDIFF_T, sizeof (X))


/* Stored state numbers (used for stacks). */
typedef yytype_int8 yy_state_t;

/* State numbers in computations.  */
typedef int yy_state_fast_t;

#ifndef YY_
# if defined YYENABLE_NLS && YYENABLE_NLS
#  if ENABLE_NLS
#   include <libintl.h> /* INFRINGES ON USER NAME SPACE */
#   define YY_(Msgid) dgettext ("bison-runtime", Msgid)
#  endif
# endif
# ifndef YY_
#  define YY_(Msgid) Msgid
# endif
#endif


#ifndef YY_ATTRIBUTE_PURE
# if defined __GNUC__ && 2 < __GNUC__ + (96 <= __GNUC_MINOR__)
#  define YY_ATTRIBUTE_PURE __attribute__ ((__pure__))
# else
#  define YY_ATTRIBUTE_PURE
# endif
#endif

#ifndef YY_ATTRIBUTE_UNUSED
# if defined __GNUC__ && 2 < __GNUC__ + (7 <= __GNUC_MINOR__)
#  define YY_ATTRIBUTE_UNUSED __attribute__ ((__unused__))
# else
#  define YY_ATTRIBUTE_UNUSED
# endif
#endif

/* Suppress unused-variable warnings by "using" E.  */
#if ! defined lint || defined __GNUC__
# define YY_USE(E) ((void) (E))
#else
# define YY_USE(E) /* empty */
#endif

/* Suppress an incorrect diagnostic about yylval being uninitialized.  */
#if defined __GNUC__ && ! defined __ICC && 406 <= __GNUC__ * 100 + __GNUC_MINOR__
# if __GNUC__ * 100 + __GNUC_MINOR__ < 407
#  define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN                           \
    _Pragma ("GCC diagnostic push")                                     \
    _Pragma ("GCC diagnostic ignored \"-Wuninitialized\"")
# else
#  define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN                           \
    _Pragma ("GCC diagnostic push")                                     \
    _Pragma ("GCC diagnostic ignored \"-Wuninitialized\"")              \
    _Pragma ("GCC diagnostic ignored \"-Wmaybe-uninitialized\"")
# endif
# define YY_IGNORE_MAYBE_UNINITIALIZED_END      \
    _Pragma ("GCC diagnostic pop")
#else
# define YY_INITIAL_VALUE(Value) Value
#endif
#ifndef YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
# define YY_IGNORE_MAYBE_UNINITIALIZED_END
#endif
#ifndef YY_INITIAL_VALUE
# define YY_INITIAL_VALUE(Value) /* Nothing. */
#endif

#if defined __cplusplus && defined __GNUC__ && ! defined __ICC && 6 <= __GNUC__
# define YY_IGNORE_USELESS_CAST_BEGIN                          \
    _Pragma ("GCC diagnostic push")                            \
    _Pragma ("GCC diagnostic ignored \"-Wuseless-cast\"")
# define YY_IGNORE_USELESS_CAST_END            \
    _Pragma ("GCC diagnostic pop")
#endif
#ifndef YY_IGNORE_USELESS_CAST_BEGIN
# define YY_IGNORE_USELESS_CAST_BEGIN
# define YY_IGNORE_USELESS_CAST_END
#endif


#define YY_ASSERT(E) ((void) (0 && (E)))

#if 1

/* The parser invokes alloca or malloc; define the necessary symbols.  */

# ifdef YYSTACK_USE_ALLOCA
#  if YYSTACK_USE_ALLOCA
#   ifdef __GNUC__
#    define YYSTACK_ALLOC __builtin_alloca
#   elif defined __BUILTIN_VA_ARG_INCR
#    include <alloca.h> /* INFRINGES ON USER NAME SPACE */
#   elif defined _AIX
#    define YYSTACK_ALLOC __alloca
#   elif defined _MSC_VER
#    include <malloc.h> /* INFRINGES ON USER NAME SPACE */
#    define alloca _alloca
#   else
#    define YYSTACK_ALLOC alloca
#    if ! defined _ALLOCA_H && ! defined EXIT_SUCCESS
#     include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
      /* Use EXIT_SUCCESS as a witness for stdlib.h.  */
#     ifndef EXIT_SUCCESS
#      define EXIT_SUCCESS 0
#     endif
#    endif
#   endif
#  endif
# endif

# ifdef YYSTACK_ALLOC
   /* Pacify GCC's 'empty if-body' warning.  */
#  define YYSTACK_FREE(Ptr) do { /* empty */; } while (0)
#  ifndef YYSTACK_ALLOC_MAXIMUM
    /* The OS might guarantee only one guard page at the bottom of the stack,
       and a page size can be as small as 4096 bytes.  So we cannot safely
       invoke alloca (N) if N exceeds 4096.  Use a slightly smaller number
       to allow for a few compiler-allocated temporary stack slots.  */
#   define YYSTACK_ALLOC_MAXIMUM 4032 /* reasonable circa 2006 */
#  endif
# else
#  define YYSTACK_ALLOC YYMALLOC
#  define YYSTACK_FREE YYFREE
#  ifndef YYSTACK_ALLOC_MAXIMUM
#   define YYSTACK_ALLOC_MAXIMUM YYSIZE_MAXIMUM
#  endif
#  if (defined __cplusplus && ! defined EXIT_SUCCESS \
       && ! ((defined YYMALLOC || defined malloc) \
             && (defined YYFREE || defined free)))
#   include <stdlib.h> /* INFRINGES ON USER NAME SPACE */
#   ifndef EXIT_SUCCESS
#    define EXIT_SUCCESS 0
#   endif
#  endif
#  ifndef YYMALLOC
#   define YYMALLOC malloc
#   if ! defined malloc && ! defined EXIT_SUCCESS
void *malloc (YYSIZE_T); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
#  ifndef YYFREE
#   define YYFREE free
#   if ! defined free && ! defined EXIT_SUCCESS
void free (void *); /* INFRINGES ON USER NAME SPACE */
#   endif
#  endif
# endif
#endif /* 1 */

#if (! defined yyoverflow \
     && (! defined __cplusplus \
         || (defined YYSTYPE_IS_TRIVIAL && YYSTYPE_IS_TRIVIAL)))

/* A type that is properly aligned for any stack member.  */
union yyalloc
{
  yy_state_t yyss_alloc;
  YYSTYPE yyvs_alloc;
};

/* The size of the maximum gap between one aligned stack and the next.  */
# define YYSTACK_GAP_MAXIMUM (YYSIZEOF (union yyalloc) - 1)

/* The size of an array large to enough to hold all stacks, each with
   N elements.  */
# define YYSTACK_BYTES(N) \
     ((N) * (YYSIZEOF (yy_state_t) + YYSIZEOF (YYSTYPE)) \
      + YYSTACK_GAP_MAXIMUM)

# define YYCOPY_NEEDED 1

/* Relocate STACK from its old location to the new one.  The
   local variables YYSIZE and YYSTACKSIZE give the old and new number of
   elements in the stack, and YYPTR gives the new location of the
   stack.  Advance YYPTR to a properly aligned location for the next
   stack.  */
# define YYSTACK_RELOCATE(Stack_alloc, Stack)                           \
    do                                                                  \
      {                                                                 \
        YYPTRDIFF_T yynewbytes;                                         \
        YYCOPY (&yyptr->Stack_alloc, Stack, yysize);                    \
        Stack = &yyptr->Stack_alloc;                                    \
        yynewbytes = yystacksize * YYSIZEOF (*Stack) + YYSTACK_GAP_MAXIMUM; \
        yyptr += yynewbytes / YYSIZEOF (*yyptr);                        \
      }                                                                 \
    while (0)

#endif

#if defined YYCOPY_NEEDED && YYCOPY_NEEDED
/* Copy COUNT objects from SRC to DST.  The source and destination do
   not overlap.  */
# ifndef YYCOPY
#  if defined __GNUC__ && 1 < __GNUC__
#   define YYCOPY(Dst, Src, Count) \
      __builtin_memcpy (Dst, Src, YY_CAST (YYSIZE_T, (Count)) * sizeof (*(Src)))
#  else
#   define YYCOPY(Dst, Src, Count)              \
      do                                        \
        {                                       \
          YYPTRDIFF_T yyi;                      \
          for (yyi = 0; yyi < (Count); yyi++)   \
            (Dst)[yyi] = (Src)[yyi];            \
        }                                       \
      while (0)
#  endif
# endif
#endif /* !YYCOPY_NEEDED */

/* YYFINAL -- State number of the termination state.  */
#define YYFINAL  5
/* YYLAST -- Last index in YYTABLE.  */
#define YYLAST   87

/* YYNTOKENS -- Number of terminals.  */
#define YYNTOKENS  20
/* YYNNTS -- Number of nonterminals.  */
#define YYNNTS  16
/* YYNRULES -- Number of rules.  */
#define YYNRULES  37
/* YYNSTATES -- Number of states.  */
#define YYNSTATES  80

/* YYMAXUTOK -- Last valid token kind.  */
#define YYMAXUTOK   274


/* YYTRANSLATE(TOKEN-NUM) -- Symbol number corresponding to TOKEN-NUM
   as returned by yylex, with out-of-bounds checking.  */
#define YYTRANSLATE(YYX)                                \
  (0 <= (YYX) && (YYX) <= YYMAXUTOK                     \
   ? YY_CAST (yysymbol_kind_t, yytranslate[YYX])        \
   : YYSYMBOL_YYUNDEF)

/* YYTRANSLATE[TOKEN-NUM] -- Symbol number corresponding to TOKEN-NUM
   as returned by yylex.  */
static const yytype_int8 yytranslate[] =
{
       0,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     2,     2,     2,     2,
       2,     2,     2,     2,     2,     2,     1,     2,     3,     4,
       5,     6,     7,     8,     9,    10,    11,    12,    13,    14,
      15,    16,    17,    18,    19
};

#if YYDEBUG
/* YYRLINE[YYN] -- Source line where rule number YYN was defined.  */
static const yytype_uint8 yyrline[] =
{
       0,    74,    74,    74,    74,    75,    75,    75,    77,    77,
      79,    79,    83,    83,    87,    92,   129,   139,   144,   155,
     160,   176,   181,   198,   198,   198,   200,   200,   200,   202,
     211,   220,   224,   244,   246,   248,   250,   252
};
#endif

/** Accessing symbol of state STATE.  */
#define YY_ACCESSING_SYMBOL(State) YY_CAST (yysymbol_kind_t, yystos[State])

#if 1
/* The user-facing name of the symbol whose (internal) number is
   YYSYMBOL.  No bounds checking.  */
static const char *yysymbol_name (yysymbol_kind_t yysymbol) YY_ATTRIBUTE_UNUSED;

static const char *
yysymbol_name (yysymbol_kind_t yysymbol)
{
  static const char *const yy_sname[] =
  {
  "end of file", "error", "invalid token", "INICIO", "FIN", "ESCRIBIR",
  "LEER", "INT", "STRING", "CONST", "CONSTANTE_NUM", "OP_ASIG", "OP_SUMA",
  "OP_RESTA", "P_IZQUIERDO", "P_DERECHO", "PUNTOYCOMA", "COMA",
  "IDENTIFICADOR", "CONSTANTE_LIT", "$accept", "programa", "$@1", "$@2",
  "$@3", "$@4", "lista_sentencias", "sentencia", "$@5", "$@6", "listaIds",
  "$@7", "listaExpresiones", "$@8", "expresion", "primaria", YY_NULLPTR
  };
  return yy_sname[yysymbol];
}
#endif

#define YYPACT_NINF (-22)

#define yypact_value_is_default(Yyn) \
  ((Yyn) == YYPACT_NINF)

#define YYTABLE_NINF (-29)

#define yytable_value_is_error(Yyn) \
  0

/* YYPACT[STATE-NUM] -- Index in YYTABLE of the portion describing
   STATE-NUM.  */
static const yytype_int8 yypact[] =
{
      -2,     1,    15,     4,    34,   -22,   -22,   -22,     7,    30,
      -4,    44,    12,   -22,   -22,    46,    48,    -9,    17,    54,
      57,    21,   -22,   -22,    66,    21,    58,    21,   -22,    21,
     -22,    25,    26,   -22,    49,    21,   -22,   -22,    31,   -22,
      70,   -22,    62,    11,    63,    64,    33,    38,    21,   -22,
      21,   -22,   -22,    21,    52,    21,    21,   -22,   -22,    65,
      67,    68,    71,   -22,   -22,    40,    45,    56,   -22,   -22,
     -22,   -22,    21,    58,   -22,   -22,   -22,   -22,   -22,   -22
};

/* YYDEFACT[STATE-NUM] -- Default reduction number in state STATE-NUM.
   Performed when YYTABLE does not specify something else to do.  Zero
   means the default is an error.  */
static const yytype_int8 yydefact[] =
{
       0,     2,     0,     0,     0,     1,    12,    10,     0,     0,
       0,     0,     0,     9,     6,     0,     0,     0,     0,     0,
       0,     0,     3,     8,     0,     0,     0,     0,    14,     0,
      19,     0,     0,    34,     0,     0,    32,    33,     0,    31,
       0,     7,     0,    26,    23,     0,     0,     0,     0,    17,
       0,    21,    35,     0,     0,     0,     0,    15,     4,     0,
       0,     0,     0,    16,    20,     0,     0,     0,    36,    29,
      30,    13,     0,     0,    11,    18,    22,    37,    27,    24
};

/* YYPGOTO[NTERM-NUM].  */
static const yytype_int8 yypgoto[] =
{
     -22,   -22,   -22,   -22,   -22,   -22,   -22,    74,   -22,   -22,
       9,   -22,     8,   -22,   -21,    18
};

/* YYDEFGOTO[NTERM-NUM].  */
static const yytype_int8 yydefgoto[] =
{
       0,     2,     3,    40,     4,    24,    12,    13,    16,    15,
      45,    61,    42,    60,    43,    39
};

/* YYTABLE[YYPACT[STATE-NUM]] -- What to do in state STATE-NUM.  If
   positive, shift that token.  If negative, reduce the rule whose
   number is the opposite.  If YYTABLE_NINF, syntax error.  */
static const yytype_int8 yytable[] =
{
      38,     1,    27,    19,    20,    -5,    46,    28,    47,     6,
       7,     8,     9,    10,    54,     5,    22,     6,     7,     8,
       9,    10,    11,    55,    56,    17,   -28,    65,    29,    66,
      11,    33,    67,    30,    34,    35,    48,    50,    14,    36,
      37,    49,    51,    55,    56,    55,    56,    57,    18,    63,
      55,    56,    55,    56,    64,    21,    75,    55,    56,    52,
      25,    76,    26,    53,    55,    56,    41,    68,    55,    56,
      58,    77,    31,    69,    70,    32,    44,    59,   -25,    62,
      78,    71,    79,     0,    72,    73,    23,    74
};

static const yytype_int8 yycheck[] =
{
      21,     3,    11,     7,     8,     4,    27,    16,    29,     5,
       6,     7,     8,     9,    35,     0,     4,     5,     6,     7,
       8,     9,    18,    12,    13,    18,    15,    48,    11,    50,
      18,    10,    53,    16,    13,    14,    11,    11,     4,    18,
      19,    16,    16,    12,    13,    12,    13,    16,    18,    16,
      12,    13,    12,    13,    16,    11,    16,    12,    13,    10,
      14,    16,    14,    14,    12,    13,     0,    15,    12,    13,
       0,    15,    18,    55,    56,    18,    18,    15,    15,    15,
      72,    16,    73,    -1,    17,    17,    12,    16
};

/* YYSTOS[STATE-NUM] -- The symbol kind of the accessing symbol of
   state STATE-NUM.  */
static const yytype_int8 yystos[] =
{
       0,     3,    21,    22,    24,     0,     5,     6,     7,     8,
       9,    18,    26,    27,     4,    29,    28,    18,    18,     7,
       8,    11,     4,    27,    25,    14,    14,    11,    16,    11,
      16,    18,    18,    10,    13,    14,    18,    19,    34,    35,
      23,     0,    32,    34,    18,    30,    34,    34,    11,    16,
      11,    16,    10,    14,    34,    12,    13,    16,     0,    15,
      33,    31,    15,    16,    16,    34,    34,    34,    15,    35,
      35,    16,    17,    17,    16,    16,    16,    15,    32,    30
};

/* YYR1[RULE-NUM] -- Symbol kind of the left-hand side of rule RULE-NUM.  */
static const yytype_int8 yyr1[] =
{
       0,    20,    22,    23,    21,    24,    25,    21,    26,    26,
      28,    27,    29,    27,    27,    27,    27,    27,    27,    27,
      27,    27,    27,    31,    30,    30,    33,    32,    32,    34,
      34,    34,    35,    35,    35,    35,    35,    35
};

/* YYR2[RULE-NUM] -- Number of symbols on the right-hand side of rule RULE-NUM.  */
static const yytype_int8 yyr2[] =
{
       0,     2,     0,     0,     6,     0,     0,     5,     2,     1,
       0,     6,     0,     6,     3,     4,     5,     4,     6,     3,
       5,     4,     6,     0,     4,     1,     0,     4,     1,     3,
       3,     1,     1,     1,     1,     2,     3,     4
};


enum { YYENOMEM = -2 };

#define yyerrok         (yyerrstatus = 0)
#define yyclearin       (yychar = YYEMPTY)

#define YYACCEPT        goto yyacceptlab
#define YYABORT         goto yyabortlab
#define YYERROR         goto yyerrorlab
#define YYNOMEM         goto yyexhaustedlab


#define YYRECOVERING()  (!!yyerrstatus)

#define YYBACKUP(Token, Value)                                    \
  do                                                              \
    if (yychar == YYEMPTY)                                        \
      {                                                           \
        yychar = (Token);                                         \
        yylval = (Value);                                         \
        YYPOPSTACK (yylen);                                       \
        yystate = *yyssp;                                         \
        goto yybackup;                                            \
      }                                                           \
    else                                                          \
      {                                                           \
        yyerror (YY_("syntax error: cannot back up")); \
        YYERROR;                                                  \
      }                                                           \
  while (0)

/* Backward compatibility with an undocumented macro.
   Use YYerror or YYUNDEF. */
#define YYERRCODE YYUNDEF


/* Enable debugging if requested.  */
#if YYDEBUG

# ifndef YYFPRINTF
#  include <stdio.h> /* INFRINGES ON USER NAME SPACE */
#  define YYFPRINTF fprintf
# endif

# define YYDPRINTF(Args)                        \
do {                                            \
  if (yydebug)                                  \
    YYFPRINTF Args;                             \
} while (0)




# define YY_SYMBOL_PRINT(Title, Kind, Value, Location)                    \
do {                                                                      \
  if (yydebug)                                                            \
    {                                                                     \
      YYFPRINTF (stderr, "%s ", Title);                                   \
      yy_symbol_print (stderr,                                            \
                  Kind, Value); \
      YYFPRINTF (stderr, "\n");                                           \
    }                                                                     \
} while (0)


/*-----------------------------------.
| Print this symbol's value on YYO.  |
`-----------------------------------*/

static void
yy_symbol_value_print (FILE *yyo,
                       yysymbol_kind_t yykind, YYSTYPE const * const yyvaluep)
{
  FILE *yyoutput = yyo;
  YY_USE (yyoutput);
  if (!yyvaluep)
    return;
  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  YY_USE (yykind);
  YY_IGNORE_MAYBE_UNINITIALIZED_END
}


/*---------------------------.
| Print this symbol on YYO.  |
`---------------------------*/

static void
yy_symbol_print (FILE *yyo,
                 yysymbol_kind_t yykind, YYSTYPE const * const yyvaluep)
{
  YYFPRINTF (yyo, "%s %s (",
             yykind < YYNTOKENS ? "token" : "nterm", yysymbol_name (yykind));

  yy_symbol_value_print (yyo, yykind, yyvaluep);
  YYFPRINTF (yyo, ")");
}

/*------------------------------------------------------------------.
| yy_stack_print -- Print the state stack from its BOTTOM up to its |
| TOP (included).                                                   |
`------------------------------------------------------------------*/

static void
yy_stack_print (yy_state_t *yybottom, yy_state_t *yytop)
{
  YYFPRINTF (stderr, "Stack now");
  for (; yybottom <= yytop; yybottom++)
    {
      int yybot = *yybottom;
      YYFPRINTF (stderr, " %d", yybot);
    }
  YYFPRINTF (stderr, "\n");
}

# define YY_STACK_PRINT(Bottom, Top)                            \
do {                                                            \
  if (yydebug)                                                  \
    yy_stack_print ((Bottom), (Top));                           \
} while (0)


/*------------------------------------------------.
| Report that the YYRULE is going to be reduced.  |
`------------------------------------------------*/

static void
yy_reduce_print (yy_state_t *yyssp, YYSTYPE *yyvsp,
                 int yyrule)
{
  int yylno = yyrline[yyrule];
  int yynrhs = yyr2[yyrule];
  int yyi;
  YYFPRINTF (stderr, "Reducing stack by rule %d (line %d):\n",
             yyrule - 1, yylno);
  /* The symbols being reduced.  */
  for (yyi = 0; yyi < yynrhs; yyi++)
    {
      YYFPRINTF (stderr, "   $%d = ", yyi + 1);
      yy_symbol_print (stderr,
                       YY_ACCESSING_SYMBOL (+yyssp[yyi + 1 - yynrhs]),
                       &yyvsp[(yyi + 1) - (yynrhs)]);
      YYFPRINTF (stderr, "\n");
    }
}

# define YY_REDUCE_PRINT(Rule)          \
do {                                    \
  if (yydebug)                          \
    yy_reduce_print (yyssp, yyvsp, Rule); \
} while (0)

/* Nonzero means print parse trace.  It is left uninitialized so that
   multiple parsers can coexist.  */
int yydebug;
#else /* !YYDEBUG */
# define YYDPRINTF(Args) ((void) 0)
# define YY_SYMBOL_PRINT(Title, Kind, Value, Location)
# define YY_STACK_PRINT(Bottom, Top)
# define YY_REDUCE_PRINT(Rule)
#endif /* !YYDEBUG */


/* YYINITDEPTH -- initial size of the parser's stacks.  */
#ifndef YYINITDEPTH
# define YYINITDEPTH 200
#endif

/* YYMAXDEPTH -- maximum size the stacks can grow to (effective only
   if the built-in stack extension method is used).

   Do not make this value too large; the results are undefined if
   YYSTACK_ALLOC_MAXIMUM < YYSTACK_BYTES (YYMAXDEPTH)
   evaluated with infinite-precision integer arithmetic.  */

#ifndef YYMAXDEPTH
# define YYMAXDEPTH 10000
#endif


/* Context of a parse error.  */
typedef struct
{
  yy_state_t *yyssp;
  yysymbol_kind_t yytoken;
} yypcontext_t;

/* Put in YYARG at most YYARGN of the expected tokens given the
   current YYCTX, and return the number of tokens stored in YYARG.  If
   YYARG is null, return the number of expected tokens (guaranteed to
   be less than YYNTOKENS).  Return YYENOMEM on memory exhaustion.
   Return 0 if there are more than YYARGN expected tokens, yet fill
   YYARG up to YYARGN. */
static int
yypcontext_expected_tokens (const yypcontext_t *yyctx,
                            yysymbol_kind_t yyarg[], int yyargn)
{
  /* Actual size of YYARG. */
  int yycount = 0;
  int yyn = yypact[+*yyctx->yyssp];
  if (!yypact_value_is_default (yyn))
    {
      /* Start YYX at -YYN if negative to avoid negative indexes in
         YYCHECK.  In other words, skip the first -YYN actions for
         this state because they are default actions.  */
      int yyxbegin = yyn < 0 ? -yyn : 0;
      /* Stay within bounds of both yycheck and yytname.  */
      int yychecklim = YYLAST - yyn + 1;
      int yyxend = yychecklim < YYNTOKENS ? yychecklim : YYNTOKENS;
      int yyx;
      for (yyx = yyxbegin; yyx < yyxend; ++yyx)
        if (yycheck[yyx + yyn] == yyx && yyx != YYSYMBOL_YYerror
            && !yytable_value_is_error (yytable[yyx + yyn]))
          {
            if (!yyarg)
              ++yycount;
            else if (yycount == yyargn)
              return 0;
            else
              yyarg[yycount++] = YY_CAST (yysymbol_kind_t, yyx);
          }
    }
  if (yyarg && yycount == 0 && 0 < yyargn)
    yyarg[0] = YYSYMBOL_YYEMPTY;
  return yycount;
}




#ifndef yystrlen
# if defined __GLIBC__ && defined _STRING_H
#  define yystrlen(S) (YY_CAST (YYPTRDIFF_T, strlen (S)))
# else
/* Return the length of YYSTR.  */
static YYPTRDIFF_T
yystrlen (const char *yystr)
{
  YYPTRDIFF_T yylen;
  for (yylen = 0; yystr[yylen]; yylen++)
    continue;
  return yylen;
}
# endif
#endif

#ifndef yystpcpy
# if defined __GLIBC__ && defined _STRING_H && defined _GNU_SOURCE
#  define yystpcpy stpcpy
# else
/* Copy YYSRC to YYDEST, returning the address of the terminating '\0' in
   YYDEST.  */
static char *
yystpcpy (char *yydest, const char *yysrc)
{
  char *yyd = yydest;
  const char *yys = yysrc;

  while ((*yyd++ = *yys++) != '\0')
    continue;

  return yyd - 1;
}
# endif
#endif



static int
yy_syntax_error_arguments (const yypcontext_t *yyctx,
                           yysymbol_kind_t yyarg[], int yyargn)
{
  /* Actual size of YYARG. */
  int yycount = 0;
  /* There are many possibilities here to consider:
     - If this state is a consistent state with a default action, then
       the only way this function was invoked is if the default action
       is an error action.  In that case, don't check for expected
       tokens because there are none.
     - The only way there can be no lookahead present (in yychar) is if
       this state is a consistent state with a default action.  Thus,
       detecting the absence of a lookahead is sufficient to determine
       that there is no unexpected or expected token to report.  In that
       case, just report a simple "syntax error".
     - Don't assume there isn't a lookahead just because this state is a
       consistent state with a default action.  There might have been a
       previous inconsistent state, consistent state with a non-default
       action, or user semantic action that manipulated yychar.
     - Of course, the expected token list depends on states to have
       correct lookahead information, and it depends on the parser not
       to perform extra reductions after fetching a lookahead from the
       scanner and before detecting a syntax error.  Thus, state merging
       (from LALR or IELR) and default reductions corrupt the expected
       token list.  However, the list is correct for canonical LR with
       one exception: it will still contain any token that will not be
       accepted due to an error action in a later state.
  */
  if (yyctx->yytoken != YYSYMBOL_YYEMPTY)
    {
      int yyn;
      if (yyarg)
        yyarg[yycount] = yyctx->yytoken;
      ++yycount;
      yyn = yypcontext_expected_tokens (yyctx,
                                        yyarg ? yyarg + 1 : yyarg, yyargn - 1);
      if (yyn == YYENOMEM)
        return YYENOMEM;
      else
        yycount += yyn;
    }
  return yycount;
}

/* Copy into *YYMSG, which is of size *YYMSG_ALLOC, an error message
   about the unexpected token YYTOKEN for the state stack whose top is
   YYSSP.

   Return 0 if *YYMSG was successfully written.  Return -1 if *YYMSG is
   not large enough to hold the message.  In that case, also set
   *YYMSG_ALLOC to the required number of bytes.  Return YYENOMEM if the
   required number of bytes is too large to store.  */
static int
yysyntax_error (YYPTRDIFF_T *yymsg_alloc, char **yymsg,
                const yypcontext_t *yyctx)
{
  enum { YYARGS_MAX = 5 };
  /* Internationalized format string. */
  const char *yyformat = YY_NULLPTR;
  /* Arguments of yyformat: reported tokens (one for the "unexpected",
     one per "expected"). */
  yysymbol_kind_t yyarg[YYARGS_MAX];
  /* Cumulated lengths of YYARG.  */
  YYPTRDIFF_T yysize = 0;

  /* Actual size of YYARG. */
  int yycount = yy_syntax_error_arguments (yyctx, yyarg, YYARGS_MAX);
  if (yycount == YYENOMEM)
    return YYENOMEM;

  switch (yycount)
    {
#define YYCASE_(N, S)                       \
      case N:                               \
        yyformat = S;                       \
        break
    default: /* Avoid compiler warnings. */
      YYCASE_(0, YY_("syntax error"));
      YYCASE_(1, YY_("syntax error, unexpected %s"));
      YYCASE_(2, YY_("syntax error, unexpected %s, expecting %s"));
      YYCASE_(3, YY_("syntax error, unexpected %s, expecting %s or %s"));
      YYCASE_(4, YY_("syntax error, unexpected %s, expecting %s or %s or %s"));
      YYCASE_(5, YY_("syntax error, unexpected %s, expecting %s or %s or %s or %s"));
#undef YYCASE_
    }

  /* Compute error message size.  Don't count the "%s"s, but reserve
     room for the terminator.  */
  yysize = yystrlen (yyformat) - 2 * yycount + 1;
  {
    int yyi;
    for (yyi = 0; yyi < yycount; ++yyi)
      {
        YYPTRDIFF_T yysize1
          = yysize + yystrlen (yysymbol_name (yyarg[yyi]));
        if (yysize <= yysize1 && yysize1 <= YYSTACK_ALLOC_MAXIMUM)
          yysize = yysize1;
        else
          return YYENOMEM;
      }
  }

  if (*yymsg_alloc < yysize)
    {
      *yymsg_alloc = 2 * yysize;
      if (! (yysize <= *yymsg_alloc
             && *yymsg_alloc <= YYSTACK_ALLOC_MAXIMUM))
        *yymsg_alloc = YYSTACK_ALLOC_MAXIMUM;
      return -1;
    }

  /* Avoid sprintf, as that infringes on the user's name space.
     Don't have undefined behavior even if the translation
     produced a string with the wrong number of "%s"s.  */
  {
    char *yyp = *yymsg;
    int yyi = 0;
    while ((*yyp = *yyformat) != '\0')
      if (*yyp == '%' && yyformat[1] == 's' && yyi < yycount)
        {
          yyp = yystpcpy (yyp, yysymbol_name (yyarg[yyi++]));
          yyformat += 2;
        }
      else
        {
          ++yyp;
          ++yyformat;
        }
  }
  return 0;
}


/*-----------------------------------------------.
| Release the memory associated to this symbol.  |
`-----------------------------------------------*/

static void
yydestruct (const char *yymsg,
            yysymbol_kind_t yykind, YYSTYPE *yyvaluep)
{
  YY_USE (yyvaluep);
  if (!yymsg)
    yymsg = "Deleting";
  YY_SYMBOL_PRINT (yymsg, yykind, yyvaluep, yylocationp);

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  YY_USE (yykind);
  YY_IGNORE_MAYBE_UNINITIALIZED_END
}


/* Lookahead token kind.  */
int yychar;

/* The semantic value of the lookahead symbol.  */
YYSTYPE yylval;
/* Number of syntax errors so far.  */
int yynerrs;




/*----------.
| yyparse.  |
`----------*/

int
yyparse (void)
{
    yy_state_fast_t yystate = 0;
    /* Number of tokens to shift before error messages enabled.  */
    int yyerrstatus = 0;

    /* Refer to the stacks through separate pointers, to allow yyoverflow
       to reallocate them elsewhere.  */

    /* Their size.  */
    YYPTRDIFF_T yystacksize = YYINITDEPTH;

    /* The state stack: array, bottom, top.  */
    yy_state_t yyssa[YYINITDEPTH];
    yy_state_t *yyss = yyssa;
    yy_state_t *yyssp = yyss;

    /* The semantic value stack: array, bottom, top.  */
    YYSTYPE yyvsa[YYINITDEPTH];
    YYSTYPE *yyvs = yyvsa;
    YYSTYPE *yyvsp = yyvs;

  int yyn;
  /* The return value of yyparse.  */
  int yyresult;
  /* Lookahead symbol kind.  */
  yysymbol_kind_t yytoken = YYSYMBOL_YYEMPTY;
  /* The variables used to return semantic value and location from the
     action routines.  */
  YYSTYPE yyval;

  /* Buffer for error messages, and its allocated size.  */
  char yymsgbuf[128];
  char *yymsg = yymsgbuf;
  YYPTRDIFF_T yymsg_alloc = sizeof yymsgbuf;

#define YYPOPSTACK(N)   (yyvsp -= (N), yyssp -= (N))

  /* The number of symbols on the RHS of the reduced rule.
     Keep to zero when no symbol should be popped.  */
  int yylen = 0;

  YYDPRINTF ((stderr, "Starting parse\n"));

  yychar = YYEMPTY; /* Cause a token to be read.  */

  goto yysetstate;


/*------------------------------------------------------------.
| yynewstate -- push a new state, which is found in yystate.  |
`------------------------------------------------------------*/
yynewstate:
  /* In all cases, when you get here, the value and location stacks
     have just been pushed.  So pushing a state here evens the stacks.  */
  yyssp++;


/*--------------------------------------------------------------------.
| yysetstate -- set current state (the top of the stack) to yystate.  |
`--------------------------------------------------------------------*/
yysetstate:
  YYDPRINTF ((stderr, "Entering state %d\n", yystate));
  YY_ASSERT (0 <= yystate && yystate < YYNSTATES);
  YY_IGNORE_USELESS_CAST_BEGIN
  *yyssp = YY_CAST (yy_state_t, yystate);
  YY_IGNORE_USELESS_CAST_END
  YY_STACK_PRINT (yyss, yyssp);

  if (yyss + yystacksize - 1 <= yyssp)
#if !defined yyoverflow && !defined YYSTACK_RELOCATE
    YYNOMEM;
#else
    {
      /* Get the current used size of the three stacks, in elements.  */
      YYPTRDIFF_T yysize = yyssp - yyss + 1;

# if defined yyoverflow
      {
        /* Give user a chance to reallocate the stack.  Use copies of
           these so that the &'s don't force the real ones into
           memory.  */
        yy_state_t *yyss1 = yyss;
        YYSTYPE *yyvs1 = yyvs;

        /* Each stack pointer address is followed by the size of the
           data in use in that stack, in bytes.  This used to be a
           conditional around just the two extra args, but that might
           be undefined if yyoverflow is a macro.  */
        yyoverflow (YY_("memory exhausted"),
                    &yyss1, yysize * YYSIZEOF (*yyssp),
                    &yyvs1, yysize * YYSIZEOF (*yyvsp),
                    &yystacksize);
        yyss = yyss1;
        yyvs = yyvs1;
      }
# else /* defined YYSTACK_RELOCATE */
      /* Extend the stack our own way.  */
      if (YYMAXDEPTH <= yystacksize)
        YYNOMEM;
      yystacksize *= 2;
      if (YYMAXDEPTH < yystacksize)
        yystacksize = YYMAXDEPTH;

      {
        yy_state_t *yyss1 = yyss;
        union yyalloc *yyptr =
          YY_CAST (union yyalloc *,
                   YYSTACK_ALLOC (YY_CAST (YYSIZE_T, YYSTACK_BYTES (yystacksize))));
        if (! yyptr)
          YYNOMEM;
        YYSTACK_RELOCATE (yyss_alloc, yyss);
        YYSTACK_RELOCATE (yyvs_alloc, yyvs);
#  undef YYSTACK_RELOCATE
        if (yyss1 != yyssa)
          YYSTACK_FREE (yyss1);
      }
# endif

      yyssp = yyss + yysize - 1;
      yyvsp = yyvs + yysize - 1;

      YY_IGNORE_USELESS_CAST_BEGIN
      YYDPRINTF ((stderr, "Stack size increased to %ld\n",
                  YY_CAST (long, yystacksize)));
      YY_IGNORE_USELESS_CAST_END

      if (yyss + yystacksize - 1 <= yyssp)
        YYABORT;
    }
#endif /* !defined yyoverflow && !defined YYSTACK_RELOCATE */


  if (yystate == YYFINAL)
    YYACCEPT;

  goto yybackup;


/*-----------.
| yybackup.  |
`-----------*/
yybackup:
  /* Do appropriate processing given the current state.  Read a
     lookahead token if we need one and don't already have one.  */

  /* First try to decide what to do without reference to lookahead token.  */
  yyn = yypact[yystate];
  if (yypact_value_is_default (yyn))
    goto yydefault;

  /* Not known => get a lookahead token if don't already have one.  */

  /* YYCHAR is either empty, or end-of-input, or a valid lookahead.  */
  if (yychar == YYEMPTY)
    {
      YYDPRINTF ((stderr, "Reading a token\n"));
      yychar = yylex ();
    }

  if (yychar <= YYEOF)
    {
      yychar = YYEOF;
      yytoken = YYSYMBOL_YYEOF;
      YYDPRINTF ((stderr, "Now at end of input.\n"));
    }
  else if (yychar == YYerror)
    {
      /* The scanner already issued an error message, process directly
         to error recovery.  But do not keep the error token as
         lookahead, it is too special and may lead us to an endless
         loop in error recovery. */
      yychar = YYUNDEF;
      yytoken = YYSYMBOL_YYerror;
      goto yyerrlab1;
    }
  else
    {
      yytoken = YYTRANSLATE (yychar);
      YY_SYMBOL_PRINT ("Next token is", yytoken, &yylval, &yylloc);
    }

  /* If the proper action on seeing token YYTOKEN is to reduce or to
     detect an error, take that action.  */
  yyn += yytoken;
  if (yyn < 0 || YYLAST < yyn || yycheck[yyn] != yytoken)
    goto yydefault;
  yyn = yytable[yyn];
  if (yyn <= 0)
    {
      if (yytable_value_is_error (yyn))
        goto yyerrlab;
      yyn = -yyn;
      goto yyreduce;
    }

  /* Count tokens shifted since error; after three, turn off error
     status.  */
  if (yyerrstatus)
    yyerrstatus--;

  /* Shift the lookahead token.  */
  YY_SYMBOL_PRINT ("Shifting", yytoken, &yylval, &yylloc);
  yystate = yyn;
  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END

  /* Discard the shifted token.  */
  yychar = YYEMPTY;
  goto yynewstate;


/*-----------------------------------------------------------.
| yydefault -- do the default action for the current state.  |
`-----------------------------------------------------------*/
yydefault:
  yyn = yydefact[yystate];
  if (yyn == 0)
    goto yyerrlab;
  goto yyreduce;


/*-----------------------------.
| yyreduce -- do a reduction.  |
`-----------------------------*/
yyreduce:
  /* yyn is the number of a rule to reduce with.  */
  yylen = yyr2[yyn];

  /* If YYLEN is nonzero, implement the default value of the action:
     '$$ = $1'.

     Otherwise, the following line sets YYVAL to garbage.
     This behavior is undocumented and Bison
     users should not rely upon it.  Assigning to YYVAL
     unconditionally makes the parser a bit smaller, and it avoids a
     GCC warning that YYVAL may be used uninitialized.  */
  yyval = yyvsp[1-yylen];


  YY_REDUCE_PRINT (yyn);
  switch (yyn)
    {
  case 2: /* $@1: %empty  */
#line 74 "codigo_bison.y"
                                      {tabla_de_simbolos = malloc(sizeof(lista_enlazada)); iniciar_tabla_de_simbolos(tabla_de_simbolos);}
#line 1403 "codigo_bison.tab.c"
    break;

  case 3: /* $@2: %empty  */
#line 74 "codigo_bison.y"
                                                                                                                                                              {if(salidaEstandar) fin_de_analisis(tabla_de_simbolos); }
#line 1409 "codigo_bison.tab.c"
    break;

  case 4: /* programa: INICIO $@1 lista_sentencias FIN $@2 $end  */
#line 74 "codigo_bison.y"
                                                                                                                                                                                                                             {fin_de_analisis(tabla_de_simbolos);}
#line 1415 "codigo_bison.tab.c"
    break;

  case 5: /* $@3: %empty  */
#line 75 "codigo_bison.y"
                                      {tabla_de_simbolos = malloc(sizeof(lista_enlazada)); iniciar_tabla_de_simbolos(tabla_de_simbolos);}
#line 1421 "codigo_bison.tab.c"
    break;

  case 6: /* $@4: %empty  */
#line 75 "codigo_bison.y"
                                                                                                                                             { if(salidaEstandar) fin_de_analisis(tabla_de_simbolos);}
#line 1427 "codigo_bison.tab.c"
    break;

  case 7: /* programa: INICIO $@3 FIN $@4 $end  */
#line 75 "codigo_bison.y"
                                                                                                                                                                                                            {fin_de_analisis(tabla_de_simbolos);}
#line 1433 "codigo_bison.tab.c"
    break;

  case 10: /* $@5: %empty  */
#line 79 "codigo_bison.y"
                                    {lista_identificadores = malloc(sizeof(lista_enlazada_ids)); lista_identificadores -> head = NULL;}
#line 1439 "codigo_bison.tab.c"
    break;

  case 11: /* sentencia: LEER $@5 P_IZQUIERDO listaIds P_DERECHO PUNTOYCOMA  */
#line 79 "codigo_bison.y"
                                                                                                                                                                                 {
                                    leer_identificadores(lista_identificadores, tabla_de_simbolos);
                                    eliminar_lista_ids(lista_identificadores);
                                }
#line 1448 "codigo_bison.tab.c"
    break;

  case 12: /* $@6: %empty  */
#line 83 "codigo_bison.y"
                                        {lista_de_expresiones = malloc(sizeof(lista_enlazada_expresiones)); lista_de_expresiones -> head = NULL;}
#line 1454 "codigo_bison.tab.c"
    break;

  case 13: /* sentencia: ESCRIBIR $@6 P_IZQUIERDO listaExpresiones P_DERECHO PUNTOYCOMA  */
#line 83 "codigo_bison.y"
                                                                                                                                                                                                   {
                                    escribir_expresiones(lista_de_expresiones);
                                    eliminar_lista_expresiones(lista_de_expresiones);
                                }
#line 1463 "codigo_bison.tab.c"
    break;

  case 14: /* sentencia: INT IDENTIFICADOR PUNTOYCOMA  */
#line 87 "codigo_bison.y"
                                                            {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-1].id_val))){
                                        agregar_int_a_tabla(tabla_de_simbolos, (yyvsp[-1].id_val), 0, false);
                                    }
                                }
#line 1473 "codigo_bison.tab.c"
    break;

  case 15: /* sentencia: IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA  */
#line 92 "codigo_bison.y"
                                                                          {
                                    if(existe_el_id(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                        if(!es_una_constante(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                            switch(es_int_o_string(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                                case INTEGER:
                                                    if(((yyvsp[-1].cte_fin)).tipo == CTE_NUMERICA){
                                                        modificar_dato_entero(tabla_de_simbolos, (yyvsp[-3].id_val), ((yyvsp[-1].cte_fin)).valor_numerico);
                                                    }
                                                    else{
                                                        errorExpresionErronea(CTE_LITERARIA);
                                                    }
                                                    break;
                                                case STRINGER:
                                                    if(((yyvsp[-1].cte_fin)).tipo == CTE_LITERARIA){
                                                        if(strlen(((yyvsp[-1].cte_fin)).valor_lit) < 255){
                                                            modificar_dato_lit(tabla_de_simbolos, (yyvsp[-3].id_val), ((yyvsp[-1].cte_fin)).valor_lit);
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
                                            errorModificarCte((yyvsp[-3].id_val));
                                        }
                                    }
                                    else{
                                        errorIdNoDeclarado((yyvsp[-3].id_val));
                                    }
                                }
#line 1515 "codigo_bison.tab.c"
    break;

  case 16: /* sentencia: INT IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA  */
#line 129 "codigo_bison.y"
                                                                              {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                        if(((yyvsp[-1].cte_fin)).tipo == CTE_NUMERICA){
                                            agregar_int_a_tabla(tabla_de_simbolos, (yyvsp[-3].id_val), ((yyvsp[-1].cte_fin)).valor_numerico, false);
                                        }
                                        else{
                                            errorExpresionErronea(CTE_LITERARIA);
                                        }
                                    }
                                }
#line 1530 "codigo_bison.tab.c"
    break;

  case 17: /* sentencia: CONST INT IDENTIFICADOR PUNTOYCOMA  */
#line 139 "codigo_bison.y"
                                                                  {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-1].id_val))){
                                        agregar_int_a_tabla(tabla_de_simbolos, (yyvsp[-1].id_val), 0, true);
                                    }
                                }
#line 1540 "codigo_bison.tab.c"
    break;

  case 18: /* sentencia: CONST INT IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA  */
#line 144 "codigo_bison.y"
                                                                                    {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                        if(((yyvsp[-1].cte_fin)).tipo == CTE_NUMERICA){
                                            agregar_int_a_tabla(tabla_de_simbolos, (yyvsp[-3].id_val), ((yyvsp[-1].cte_fin)).valor_numerico, true);
                                        }
                                        else{
                                            errorExpresionErronea(CTE_LITERARIA);
                                        }
                                    }
                                    
                                }
#line 1556 "codigo_bison.tab.c"
    break;

  case 19: /* sentencia: STRING IDENTIFICADOR PUNTOYCOMA  */
#line 155 "codigo_bison.y"
                                                               {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-1].id_val))){
                                        agregar_string_a_tabla(tabla_de_simbolos, (yyvsp[-1].id_val), "", false);
                                    }
                                }
#line 1566 "codigo_bison.tab.c"
    break;

  case 20: /* sentencia: STRING IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA  */
#line 160 "codigo_bison.y"
                                                                                 {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                        if(((yyvsp[-1].cte_fin)).tipo == CTE_LITERARIA){
                                            if(strlen(((yyvsp[-1].cte_fin)).valor_lit)<255){
                                                agregar_string_a_tabla(tabla_de_simbolos, (yyvsp[-3].id_val), ((yyvsp[-1].cte_fin)).valor_lit, false);
                                            }
                                            else{
                                                errorCaracteres();
                                            }
                                            
                                        }
                                        else{
                                            errorExpresionErronea(CTE_NUMERICA);
                                        }
                                    }
                                }
#line 1587 "codigo_bison.tab.c"
    break;

  case 21: /* sentencia: CONST STRING IDENTIFICADOR PUNTOYCOMA  */
#line 176 "codigo_bison.y"
                                                                     {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-1].id_val))){
                                        agregar_string_a_tabla(tabla_de_simbolos, (yyvsp[-1].id_val), "", true);
                                    }
                                }
#line 1597 "codigo_bison.tab.c"
    break;

  case 22: /* sentencia: CONST STRING IDENTIFICADOR OP_ASIG expresion PUNTOYCOMA  */
#line 181 "codigo_bison.y"
                                                                                       {
                                    if(es_id_valido(tabla_de_simbolos, (yyvsp[-3].id_val))){
                                        if(((yyvsp[-1].cte_fin)).tipo == CTE_LITERARIA){
                                            if(strlen(((yyvsp[-1].cte_fin)).valor_lit)<255){
                                                agregar_string_a_tabla(tabla_de_simbolos, (yyvsp[-3].id_val), ((yyvsp[-1].cte_fin)).valor_lit, true);
                                            }
                                            else{
                                                errorCaracteres();
                                            }
                                            
                                        }
                                        else{
                                            errorExpresionErronea(CTE_NUMERICA);
                                        }
                                    }
                                }
#line 1618 "codigo_bison.tab.c"
    break;

  case 23: /* $@7: %empty  */
#line 198 "codigo_bison.y"
                                             {agregar_id(lista_identificadores,(yyvsp[0].id_val), tabla_de_simbolos);}
#line 1624 "codigo_bison.tab.c"
    break;

  case 24: /* listaIds: IDENTIFICADOR $@7 COMA listaIds  */
#line 198 "codigo_bison.y"
                                                                                                                     {}
#line 1630 "codigo_bison.tab.c"
    break;

  case 25: /* listaIds: IDENTIFICADOR  */
#line 198 "codigo_bison.y"
                                                                                                                                       {agregar_id(lista_identificadores,(yyvsp[0].id_val), tabla_de_simbolos);}
#line 1636 "codigo_bison.tab.c"
    break;

  case 26: /* $@8: %empty  */
#line 200 "codigo_bison.y"
                                         {agregar_expresion(lista_de_expresiones, (yyvsp[0].cte_fin));}
#line 1642 "codigo_bison.tab.c"
    break;

  case 27: /* listaExpresiones: expresion $@8 COMA listaExpresiones  */
#line 200 "codigo_bison.y"
                                                                                                             {}
#line 1648 "codigo_bison.tab.c"
    break;

  case 28: /* listaExpresiones: expresion  */
#line 200 "codigo_bison.y"
                                                                                                                           {agregar_expresion(lista_de_expresiones, (yyvsp[0].cte_fin));}
#line 1654 "codigo_bison.tab.c"
    break;

  case 29: /* expresion: expresion OP_SUMA primaria  */
#line 202 "codigo_bison.y"
                                                           {
                                        if(((yyvsp[-2].cte_fin)).tipo == CTE_LITERARIA || ((yyvsp[0].cte_fin)).tipo == CTE_LITERARIA){
                                            errorOperacionString();
                                        }
                                        else{
                                            (yyval.cte_fin).tipo = CTE_NUMERICA;
                                            (yyval.cte_fin).valor_numerico = ((yyvsp[-2].cte_fin)).valor_numerico + ((yyvsp[0].cte_fin)).valor_numerico;
                                        }
                                            }
#line 1668 "codigo_bison.tab.c"
    break;

  case 30: /* expresion: expresion OP_RESTA primaria  */
#line 211 "codigo_bison.y"
                                                            {
                                        if(((yyvsp[-2].cte_fin)).tipo == CTE_LITERARIA || ((yyvsp[0].cte_fin)).tipo == CTE_LITERARIA){
                                            errorOperacionString();
                                        }
                                        else{
                                            (yyval.cte_fin).tipo = CTE_NUMERICA;
                                            (yyval.cte_fin).valor_numerico = ((yyvsp[-2].cte_fin)).valor_numerico - ((yyvsp[0].cte_fin)).valor_numerico;
                                        }
                                            }
#line 1682 "codigo_bison.tab.c"
    break;

  case 31: /* expresion: primaria  */
#line 220 "codigo_bison.y"
                                        {
                                    (yyval.cte_fin) = (yyvsp[0].cte_fin);
                                }
#line 1690 "codigo_bison.tab.c"
    break;

  case 32: /* primaria: IDENTIFICADOR  */
#line 224 "codigo_bison.y"
                                             {
                                            nodo_lista* buscador = buscar_elemento(tabla_de_simbolos, (yyvsp[0].id_val));
                                            if(buscador == NULL){
                                                errorIdNoDeclarado((yyvsp[0].id_val));
                                            }
                                            else{
                                                switch(buscador -> tipo){
                                                    case PALABRA_CLAVE:
                                                        errorPalabraClave((yyvsp[0].id_val));
                                                        break;
                                                    case STRINGER:
                                                        (yyval.cte_fin).tipo = CTE_LITERARIA;
                                                        strcpy((yyval.cte_fin).valor_lit, buscador ->dato_lit);
                                                        break;
                                                    case INTEGER:
                                                        (yyval.cte_fin).tipo = CTE_NUMERICA;
                                                        (yyval.cte_fin).valor_numerico = buscador ->dato_entero;
                                                }
                                            }
                                                    }
#line 1715 "codigo_bison.tab.c"
    break;

  case 33: /* primaria: CONSTANTE_LIT  */
#line 244 "codigo_bison.y"
                                              {(yyval.cte_fin).tipo = CTE_LITERARIA;
                                                strcpy((yyval.cte_fin).valor_lit, (yyvsp[0].cte_lit_val));}
#line 1722 "codigo_bison.tab.c"
    break;

  case 34: /* primaria: CONSTANTE_NUM  */
#line 246 "codigo_bison.y"
                                              {(yyval.cte_fin).tipo = CTE_NUMERICA;
                                                (yyval.cte_fin).valor_numerico = (yyvsp[0].cte_num_val);}
#line 1729 "codigo_bison.tab.c"
    break;

  case 35: /* primaria: OP_RESTA CONSTANTE_NUM  */
#line 248 "codigo_bison.y"
                                                       {(yyval.cte_fin).tipo = CTE_NUMERICA;
                                                (yyval.cte_fin).valor_numerico = (yyvsp[0].cte_num_val) * (-1);}
#line 1736 "codigo_bison.tab.c"
    break;

  case 36: /* primaria: P_IZQUIERDO expresion P_DERECHO  */
#line 250 "codigo_bison.y"
                                                                {(yyval.cte_fin) = (yyvsp[-1].cte_fin);}
#line 1742 "codigo_bison.tab.c"
    break;

  case 37: /* primaria: OP_RESTA P_IZQUIERDO expresion P_DERECHO  */
#line 252 "codigo_bison.y"
                                                                         {
                                    if(((yyvsp[-1].cte_fin)).tipo == CTE_NUMERICA){
                                        (yyval.cte_fin).tipo = CTE_NUMERICA;
                                        (yyval.cte_fin).valor_numerico = (-1) * ((yyvsp[-1].cte_fin)).valor_numerico;
                                    }
                                    else{
                                        errorExpresionErronea(CTE_LITERARIA);
                                    }
                                }
#line 1756 "codigo_bison.tab.c"
    break;


#line 1760 "codigo_bison.tab.c"

      default: break;
    }
  /* User semantic actions sometimes alter yychar, and that requires
     that yytoken be updated with the new translation.  We take the
     approach of translating immediately before every use of yytoken.
     One alternative is translating here after every semantic action,
     but that translation would be missed if the semantic action invokes
     YYABORT, YYACCEPT, or YYERROR immediately after altering yychar or
     if it invokes YYBACKUP.  In the case of YYABORT or YYACCEPT, an
     incorrect destructor might then be invoked immediately.  In the
     case of YYERROR or YYBACKUP, subsequent parser actions might lead
     to an incorrect destructor call or verbose syntax error message
     before the lookahead is translated.  */
  YY_SYMBOL_PRINT ("-> $$ =", YY_CAST (yysymbol_kind_t, yyr1[yyn]), &yyval, &yyloc);

  YYPOPSTACK (yylen);
  yylen = 0;

  *++yyvsp = yyval;

  /* Now 'shift' the result of the reduction.  Determine what state
     that goes to, based on the state we popped back to and the rule
     number reduced by.  */
  {
    const int yylhs = yyr1[yyn] - YYNTOKENS;
    const int yyi = yypgoto[yylhs] + *yyssp;
    yystate = (0 <= yyi && yyi <= YYLAST && yycheck[yyi] == *yyssp
               ? yytable[yyi]
               : yydefgoto[yylhs]);
  }

  goto yynewstate;


/*--------------------------------------.
| yyerrlab -- here on detecting error.  |
`--------------------------------------*/
yyerrlab:
  /* Make sure we have latest lookahead translation.  See comments at
     user semantic actions for why this is necessary.  */
  yytoken = yychar == YYEMPTY ? YYSYMBOL_YYEMPTY : YYTRANSLATE (yychar);
  /* If not already recovering from an error, report this error.  */
  if (!yyerrstatus)
    {
      ++yynerrs;
      {
        yypcontext_t yyctx
          = {yyssp, yytoken};
        char const *yymsgp = YY_("syntax error");
        int yysyntax_error_status;
        yysyntax_error_status = yysyntax_error (&yymsg_alloc, &yymsg, &yyctx);
        if (yysyntax_error_status == 0)
          yymsgp = yymsg;
        else if (yysyntax_error_status == -1)
          {
            if (yymsg != yymsgbuf)
              YYSTACK_FREE (yymsg);
            yymsg = YY_CAST (char *,
                             YYSTACK_ALLOC (YY_CAST (YYSIZE_T, yymsg_alloc)));
            if (yymsg)
              {
                yysyntax_error_status
                  = yysyntax_error (&yymsg_alloc, &yymsg, &yyctx);
                yymsgp = yymsg;
              }
            else
              {
                yymsg = yymsgbuf;
                yymsg_alloc = sizeof yymsgbuf;
                yysyntax_error_status = YYENOMEM;
              }
          }
        yyerror (yymsgp);
        if (yysyntax_error_status == YYENOMEM)
          YYNOMEM;
      }
    }

  if (yyerrstatus == 3)
    {
      /* If just tried and failed to reuse lookahead token after an
         error, discard it.  */

      if (yychar <= YYEOF)
        {
          /* Return failure if at end of input.  */
          if (yychar == YYEOF)
            YYABORT;
        }
      else
        {
          yydestruct ("Error: discarding",
                      yytoken, &yylval);
          yychar = YYEMPTY;
        }
    }

  /* Else will try to reuse lookahead token after shifting the error
     token.  */
  goto yyerrlab1;


/*---------------------------------------------------.
| yyerrorlab -- error raised explicitly by YYERROR.  |
`---------------------------------------------------*/
yyerrorlab:
  /* Pacify compilers when the user code never invokes YYERROR and the
     label yyerrorlab therefore never appears in user code.  */
  if (0)
    YYERROR;
  ++yynerrs;

  /* Do not reclaim the symbols of the rule whose action triggered
     this YYERROR.  */
  YYPOPSTACK (yylen);
  yylen = 0;
  YY_STACK_PRINT (yyss, yyssp);
  yystate = *yyssp;
  goto yyerrlab1;


/*-------------------------------------------------------------.
| yyerrlab1 -- common code for both syntax error and YYERROR.  |
`-------------------------------------------------------------*/
yyerrlab1:
  yyerrstatus = 3;      /* Each real token shifted decrements this.  */

  /* Pop stack until we find a state that shifts the error token.  */
  for (;;)
    {
      yyn = yypact[yystate];
      if (!yypact_value_is_default (yyn))
        {
          yyn += YYSYMBOL_YYerror;
          if (0 <= yyn && yyn <= YYLAST && yycheck[yyn] == YYSYMBOL_YYerror)
            {
              yyn = yytable[yyn];
              if (0 < yyn)
                break;
            }
        }

      /* Pop the current state because it cannot handle the error token.  */
      if (yyssp == yyss)
        YYABORT;


      yydestruct ("Error: popping",
                  YY_ACCESSING_SYMBOL (yystate), yyvsp);
      YYPOPSTACK (1);
      yystate = *yyssp;
      YY_STACK_PRINT (yyss, yyssp);
    }

  YY_IGNORE_MAYBE_UNINITIALIZED_BEGIN
  *++yyvsp = yylval;
  YY_IGNORE_MAYBE_UNINITIALIZED_END


  /* Shift the error token.  */
  YY_SYMBOL_PRINT ("Shifting", YY_ACCESSING_SYMBOL (yyn), yyvsp, yylsp);

  yystate = yyn;
  goto yynewstate;


/*-------------------------------------.
| yyacceptlab -- YYACCEPT comes here.  |
`-------------------------------------*/
yyacceptlab:
  yyresult = 0;
  goto yyreturnlab;


/*-----------------------------------.
| yyabortlab -- YYABORT comes here.  |
`-----------------------------------*/
yyabortlab:
  yyresult = 1;
  goto yyreturnlab;


/*-----------------------------------------------------------.
| yyexhaustedlab -- YYNOMEM (memory exhaustion) comes here.  |
`-----------------------------------------------------------*/
yyexhaustedlab:
  yyerror (YY_("memory exhausted"));
  yyresult = 2;
  goto yyreturnlab;


/*----------------------------------------------------------.
| yyreturnlab -- parsing is finished, clean up and return.  |
`----------------------------------------------------------*/
yyreturnlab:
  if (yychar != YYEMPTY)
    {
      /* Make sure we have latest lookahead translation.  See comments at
         user semantic actions for why this is necessary.  */
      yytoken = YYTRANSLATE (yychar);
      yydestruct ("Cleanup: discarding lookahead",
                  yytoken, &yylval);
    }
  /* Do not reclaim the symbols of the rule whose action triggered
     this YYABORT or YYACCEPT.  */
  YYPOPSTACK (yylen);
  YY_STACK_PRINT (yyss, yyssp);
  while (yyssp != yyss)
    {
      yydestruct ("Cleanup: popping",
                  YY_ACCESSING_SYMBOL (+*yyssp), yyvsp);
      YYPOPSTACK (1);
    }
#ifndef yyoverflow
  if (yyss != yyssa)
    YYSTACK_FREE (yyss);
#endif
  if (yymsg != yymsgbuf)
    YYSTACK_FREE (yymsg);
  return yyresult;
}

#line 263 "codigo_bison.y"


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
