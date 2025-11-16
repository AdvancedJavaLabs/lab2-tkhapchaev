package org.example.utilities;

public class SentimentAnalysisHelper {
    public static String[] positiveWords = {
            "good", "happy", "excellent", "great", "nice", "fantastic", "wonderful", "amazing",
            "love", "joy", "pleasure", "awesome", "delightful", "brilliant", "perfect",
            "positive", "fortunate", "cheerful", "favorable", "smile", "successful", "optimistic",
            "friendly", "kind", "fun", "lucky", "enjoy", "peaceful", "satisfying", "glad",
            "inspired", "enthusiastic", "motivated", "energetic", "confident", "grateful",
            "bright", "lovely", "caring", "compassionate", "encouraging", "supportive",
            "beautiful", "creative", "trust", "hopeful", "radiant", "relaxed", "amused",
            "proud", "cheery", "joyful", "elated", "ecstatic", "thrilled", "content", "blessed",
            "jovial", "adorable", "sweet", "gentle", "generous", "welcoming", "peace", "serene",
            "glorious", "shining", "kindness", "sincere", "trustworthy", "loving", "gracious",
            "healthy", "victorious", "respected", "worthy", "pleasant", "hope", "brighten",
            "delighted", "favored", "uplifting", "cheer", "bubbly", "jubilant", "sparkling",
            "magical", "captivating", "amity", "bliss", "comfort", "companionable", "exquisite",
            "felicity", "genial", "gladness", "glee", "gratifying", "harmonious", "heartening",
            "honorable", "joviality", "lively", "marvelous", "meritorious", "peaceable",
            "pleasurable", "propitious", "rejoice", "satisfying", "sparkle", "splendid",
            "stunning", "thrilling", "tranquil", "triumphant", "vibrant", "vivacious", "zestful",
            "cheerfulness", "contentment", "delight", "euphoria", "favorable", "fortunate",
            "gleeful", "gratitude", "harmonize", "heartfelt", "hope", "inspiring", "joyous",
            "luminous", "mercy", "nurturing", "optimism", "radiance", "refreshed", "rejoicing",
            "rewarding", "satisfy", "smiling", "sunny", "thrill", "upbeat", "vitality", "warm",
            "welcome", "wonder", "wonderful", "zeal"
    };

    public static String[] negativeWords = {
            "bad", "sad", "poor", "terrible", "awful", "horrible", "worst", "hate",
            "angry", "pain", "disappointing", "unhappy", "miserable", "tragic", "unfortunate",
            "negative", "stress", "problem", "failure", "frustrated", "fear", "disgust",
            "gloomy", "depressed", "annoyed", "upset", "lonely", "hurt", "regret",
            "jealous", "bitter", "resentful", "hopeless", "confused", "stressed", "nervous",
            "pessimistic", "unlucky", "tragic", "angst", "frightened", "worried", "tense",
            "overwhelmed", "weak", "insecure", "ashamed", "embarrassed", "guilty", "displeased",
            "sorrowful", "heartbroken", "disheartened", "resentment", "discouraged", "troubled",
            "painful", "rejected", "fearful", "uncomfortable", "dismal", "grief", "anxious",
            "nasty", "critical", "hostile", "jealousy", "frustration", "rage", "sadness",
            "regretful", "offended", "upsetting", "doubtful", "humiliated", "disrespectful",
            "distressed", "threatened", "inferior", "oppressed", "weakness", "unloved",
            "angsty", "irritated", "uneasy", "gloom", "trouble", "shame", "panic", "neglect",
            "scared", "unpleasant", "tormented", "alarmed", "apprehensive", "aggravated",
            "alienated", "annoying", "appalling", "awkward", "cynical", "defeated", "defensive",
            "dejected", "despair", "desperate", "detest", "disagreeable", "disheartened",
            "disillusioned", "displeasure", "disturbing", "dread", "dull", "enraged", "exasperated",
            "fright", "grievous", "harsh", "hurtful", "hostility", "humble", "insulting",
            "irritation", "loathe", "menacing", "morose", "nagging", "nervousness", "oppressive",
            "panicked", "peril", "resent", "repulsive", "scornful", "shocking", "spiteful",
            "stressful", "tragedy", "troubling", "unhappy", "unpleasant", "upsetting",
            "vicious", "wounded", "woeful", "worthless"
    };
}