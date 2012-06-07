corpus = korpusnamn
original_dir = original
files := $(basename $(notdir $(wildcard $(original_dir)/*.xml)))

vrt_annotations = word pos msd baseform lemgram saldo prefix suffix ref dephead.ref deprel sentence.id n
vrt_columns     = word pos msd lemma    lex     saldo prefix suffix ref dephead     deprel
vrt_structs     = -    -   -   -        -       -     -      -      -   -           -      sentence:id

xml_elements    = text
xml_annotations = text
xml_skip =

include ../Makefile.common

token_chunk = sentence
token_segmenter = punkt_word

sentence_chunk = paragraph
sentence_segmenter = punkt_sentence
sentence_model = $(punkt_model)

paragraph_chunk = text
paragraph_segmenter = blanklines

################################################################################
#                                  RULES                                       #
################################################################################

include ../Makefile.rules
