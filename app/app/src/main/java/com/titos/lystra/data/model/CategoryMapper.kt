package com.titos.lystra.data.model

/**
 * Hardcoded map of common Portuguese grocery items to categories.
 * Used for automatic categorization when adding new products.
 *
 * Falls back to OUTROS for unknown products.
 * A future version could use ML or a lookup API.
 */
object CategoryMapper {

    private val categoryMap: Map<String, ProductCategory> = buildMap {
        // Laticínios
        listOf(
            "leite", "iogurte", "queijo", "manteiga", "natas", "requeijão",
            "queijo flamengo", "queijo fresco", "leite mimosa", "leite gordo",
            "leite meio gordo", "leite magro", "iogurte natural", "iogurte grego",
            "mozarela", "mozzarella", "cheddar", "parmesão", "brie",
            "queijo da serra", "queijo curado", "cream cheese",
            "leite de amêndoa", "leite de aveia", "leite de soja",
            "chantilly", "ovos", "ovo"
        ).forEach { put(it, ProductCategory.LATICINIOS) }

        // Frutas e Vegetais
        listOf(
            "banana", "bananas", "maçã", "maçãs", "laranja", "laranjas",
            "limão", "limões", "morango", "morangos", "uva", "uvas",
            "pêra", "pêras", "pêssego", "ananás", "manga", "melão",
            "melancia", "kiwi", "cereja", "cerejas", "framboesa", "mirtilo",
            "abacate", "tomate", "tomates", "alface", "cebola", "cebolas",
            "batata", "batatas", "cenoura", "cenouras", "pepino", "pimento",
            "pimentos", "brócolos", "couve", "couves", "espinafre", "alho",
            "gengibre", "cogumelos", "feijão verde", "ervilhas", "milho",
            "courgette", "beringela", "nabo", "abóbora", "salsa", "coentros",
            "hortelã", "quiabo", "agrião", "rúcula"
        ).forEach { put(it, ProductCategory.FRUTAS_VEGETAIS) }

        // Padaria
        listOf(
            "pão", "pão de forma", "pão integral", "baguete", "croissant",
            "tostas", "bolachas", "bolacha", "bolos", "bolo", "muffin",
            "tortilhas", "tortilha", "pão de alho", "broa",
            "pão ralado", "farinha", "fermento", "massa folhada"
        ).forEach { put(it, ProductCategory.PADARIA) }

        // Carnes e Peixe
        listOf(
            "frango", "peito de frango", "coxas de frango", "peru",
            "carne picada", "bife", "costeletas", "lombo", "presunto",
            "bacon", "salsicha", "salsichas", "fiambre", "chouriço",
            "alheira", "morcela", "hambúrguer", "carne de porco",
            "carne de vaca", "vitela", "borrego", "coelho",
            "salmão", "bacalhau", "atum", "pescada", "sardinha",
            "camarão", "polvo", "lulas", "dourada", "robalo",
            "gambas", "mexilhões", "delícias do mar"
        ).forEach { put(it, ProductCategory.CARNES) }

        // Congelados
        listOf(
            "pizza", "gelado", "gelados", "legumes congelados",
            "batatas fritas congeladas", "peixe congelado",
            "lasanha congelada", "ervilhas congeladas",
            "espinafre congelado", "hambúrguer congelado",
            "nuggets", "rissóis", "croquetes", "chamuças",
            "pão congelado"
        ).forEach { put(it, ProductCategory.CONGELADOS) }

        // Limpeza
        listOf(
            "detergente", "detergente roupa", "amaciador",
            "lixívia", "limpa vidros", "limpa chão",
            "desinfetante", "esfregão", "esponja", "esponjas",
            "papel higiénico", "papel cozinha", "guardanapos",
            "sacos do lixo", "película aderente", "alumínio",
            "pastilhas máquina", "sal para máquina", "abrilhantador",
            "ambientador"
        ).forEach { put(it, ProductCategory.LIMPEZA) }

        // Bebidas
        listOf(
            "água", "sumo", "sumos", "coca-cola", "pepsi",
            "cerveja", "vinho", "vinho tinto", "vinho branco",
            "café", "chá", "ice tea", "refrigerante",
            "água com gás", "tónica", "7up", "fanta",
            "soda", "kombucha", "smoothie"
        ).forEach { put(it, ProductCategory.BEBIDAS) }

        // Mercearia
        listOf(
            "arroz", "massa", "esparguete", "macarrão", "fusilli",
            "feijão", "grão", "grão-de-bico", "lentilhas", "quinoa",
            "azeite", "óleo", "vinagre", "sal", "pimenta", "açúcar",
            "mel", "compota", "doce", "nutella", "manteiga de amendoim",
            "cereais", "aveia", "granola", "muesli",
            "molho de tomate", "ketchup", "mostarda", "maionese",
            "atum em lata", "sardinha em lata", "milho em lata",
            "caldo knorr", "temperos", "orégãos", "canela",
            "chocolate", "gomas", "amêndoas", "nozes", "cajus",
            "bolachas maria", "pipocas"
        ).forEach { put(it, ProductCategory.MERCEARIA) }

        // Higiene
        listOf(
            "champô", "shampoo", "gel de banho", "sabonete",
            "pasta de dentes", "escova de dentes", "fio dental",
            "desodorizante", "creme hidratante", "protetor solar",
            "lâminas", "algodão", "pensos", "fraldas",
            "toalhitas", "lenços de papel"
        ).forEach { put(it, ProductCategory.HIGIENE) }
    }

    /**
     * Attempts to guess the category of a product from its name.
     * Uses case-insensitive matching against the known product map.
     * Falls back to OUTROS if no match is found.
     */
    fun guessCategory(productName: String): ProductCategory {
        val normalized = productName.trim().lowercase()

        // Exact match first
        categoryMap[normalized]?.let { return it }

        // Partial match: check if any key is contained in the product name
        for ((key, category) in categoryMap) {
            if (normalized.contains(key) || key.contains(normalized)) {
                return category
            }
        }

        return ProductCategory.OUTROS
    }

    /**
     * Returns the Material icon name for a given category.
     */
    fun iconForCategory(category: ProductCategory): String {
        return when (category) {
            ProductCategory.LATICINIOS -> "water_drop"
            ProductCategory.FRUTAS_VEGETAIS -> "nutrition"
            ProductCategory.PADARIA -> "bakery_dining"
            ProductCategory.CARNES -> "set_meal"
            ProductCategory.CONGELADOS -> "ac_unit"
            ProductCategory.LIMPEZA -> "cleaning_services"
            ProductCategory.BEBIDAS -> "local_cafe"
            ProductCategory.MERCEARIA -> "shopping_basket"
            ProductCategory.HIGIENE -> "spa"
            ProductCategory.OUTROS -> "shopping_cart"
        }
    }
}
