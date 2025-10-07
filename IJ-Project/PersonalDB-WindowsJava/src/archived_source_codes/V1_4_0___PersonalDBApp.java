package archived_source_codes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class V1_4_0___PersonalDBApp extends JFrame {
    private static final String DEFAULT_DATA_DIRECTORY = "C:\\PersonalDB_DATA";
    private static final File DEFAULT_CONFIG_DIRECTORY = new File("C:\\PersonalDB_CONFIG");
    private static final String GLOBAL_SCHEMA_FILE_NAME = "global_schema.psc";
    private static final String DEFAULT_PROJECT_FILE_NAME = "default_autosave.pdb";
    private static final String DEFAULT_SCHEMA_FILE_NAME = "default_global_schema.psc";

    private static final LanguageManager LANGUAGE_MANAGER = new LanguageManager();

    static {
        try {
            ensureDefaultConfigDirectory();
            ensureDefaultLanguagePacks();
            LANGUAGE_MANAGER.loadLanguagePacksFromDirectory(DEFAULT_CONFIG_DIRECTORY);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        LANGUAGE_MANAGER.setFallbackLanguage("en_US");
        LANGUAGE_MANAGER.setActiveLanguage("en_US");
    }

    private static void ensureDefaultConfigDirectory() {
        if (!DEFAULT_CONFIG_DIRECTORY.exists()) {
            DEFAULT_CONFIG_DIRECTORY.mkdirs();
        }
    }

    private static void ensureDefaultLanguagePacks() throws IOException {
        writePackIfMissing("en_US_LangPack.json", createLanguagePackJson("en_US", "English (US)", defaultEnglishTranslations()));
        writePackIfMissing("ko_KR_LangPack.json", createLanguagePackJson("ko_KR", "한국어", defaultKoreanTranslations()));
        writePackIfMissing("ja_JP_LangPack.json", createLanguagePackJson("ja_JP", "日本語", defaultJapaneseTranslations()));
    }

    private static void writePackIfMissing(String fileName, String content) throws IOException {
        File target = new File(DEFAULT_CONFIG_DIRECTORY, fileName);
        if (target.exists()) {
            return;
        }
        File parent = target.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        Files.writeString(target.toPath(), content, StandardCharsets.UTF_8);
    }

    private static String createLanguagePackJson(String code, String name, Map<String, String> translations) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"languageCode\": \"").append(escapeJson(code)).append("\",\n");
        sb.append("  \"languageName\": \"").append(escapeJson(name)).append("\"");
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            sb.append(",\n  \"").append(escapeJson(entry.getKey())).append("\": \"")
                    .append(escapeJson(entry.getValue())).append("\"");
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    private static Map<String, String> defaultEnglishTranslations() {
        Map<String, String> translations = new LinkedHashMap<>();
        translations.put("PersonalDB", "PersonalDB");
        translations.put("File", "File");
        translations.put("Edit", "Edit");
        translations.put("Config", "Config");
        translations.put("Language", "Language");
        translations.put("Help", "Help");
        translations.put("+ New Person", "+ New Person");
        translations.put("Delete", "Delete");
        translations.put("Search", "Search");
        translations.put("Compare", "Compare");
        translations.put("Clear", "Clear");
        translations.put("Language changed to %s. The application will restart.", "Language changed to %s. The application will restart.");
        translations.put("Global schema imported. Restart now?", "Global schema imported. Restart now?");
        translations.put("Global schema file is missing. Load it now? (requires restart)", "Global schema file is missing. Load it now? (requires restart)");
        translations.put("Global schema copied into the config folder. The app will restart.", "Global schema copied into the config folder. The app will restart.");
        translations.put("File not found.", "File not found.");
        translations.put("Failed to load file: %s", "Failed to load file: %s");
        translations.put("Auto-saved to %s", "Auto-saved to %s");
        translations.put("Save project before exiting?", "Save project before exiting?");
        return translations;
    }

    private static Map<String, String> defaultKoreanTranslations() {
        Map<String, String> translations = new LinkedHashMap<>();
        translations.put("PersonalDB", "PersonalDB");
        translations.put("File", "파일");
        translations.put("Edit", "편집");
        translations.put("Config", "설정");
        translations.put("Language", "언어");
        translations.put("Help", "도움말");
        translations.put("+ New Person", "+ 새 인물");
        translations.put("Delete", "삭제");
        translations.put("Search", "검색");
        translations.put("Compare", "비교");
        translations.put("Clear", "지우기");
        translations.put("Language changed to %s. The application will restart.", "%s 언어로 변경되었습니다. 프로그램이 다시 시작됩니다.");
        translations.put("Global schema imported. Restart now?", "글로벌 스키마가 가져와졌습니다. 지금 재시작할까요?");
        translations.put("Global schema file is missing. Load it now? (requires restart)", "글로벌 스키마 파일이 없습니다. 지금 불러올까요? (재시작 필요)");
        translations.put("Global schema copied into the config folder. The app will restart.", "글로벌 스키마가 설정 폴더에 복사되었습니다. 프로그램이 다시 시작됩니다.");
        translations.put("File not found.", "파일을 찾을 수 없습니다.");
        translations.put("Failed to load file: %s", "파일을 불러오지 못했습니다: %s");
        translations.put("Auto-saved to %s", "%s(으)로 자동 저장되었습니다");
        translations.put("Save project before exiting?", "종료하기 전에 프로젝트를 저장할까요?");
        return translations;
    }

    private static Map<String, String> defaultJapaneseTranslations() {
        Map<String, String> translations = new LinkedHashMap<>();
        translations.put("PersonalDB", "PersonalDB");
        translations.put("File", "ファイル");
        translations.put("Edit", "編集");
        translations.put("Config", "設定");
        translations.put("Language", "言語");
        translations.put("Help", "ヘルプ");
        translations.put("+ New Person", "+ 新規人物");
        translations.put("Delete", "削除");
        translations.put("Search", "検索");
        translations.put("Compare", "比較");
        translations.put("Clear", "クリア");
        translations.put("Language changed to %s. The application will restart.", "言語が%sに変更されました。アプリケーションが再起動します。");
        translations.put("Global schema imported. Restart now?", "グローバルスキーマを読み込みました。今すぐ再起動しますか？");
        translations.put("Global schema file is missing. Load it now? (requires restart)", "グローバルスキーマファイルが見つかりません。今すぐ読み込みますか？(再起動が必要)");
        translations.put("Global schema copied into the config folder. The app will restart.", "グローバルスキーマを設定フォルダにコピーしました。アプリケーションが再起動します。");
        translations.put("File not found.", "ファイルが見つかりません。");
        translations.put("Failed to load file: %s", "ファイルを読み込めませんでした: %s");
        translations.put("Auto-saved to %s", "%s に自動保存しました");
        translations.put("Save project before exiting?", "終了する前にプロジェクトを保存しますか？");
        return translations;
    }

    private static String escapeJson(String value) {
        StringBuilder sb = new StringBuilder();
        for (char c : value.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\\\"); break;
                case '"': sb.append("\\\""); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format(Locale.ROOT, "\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    private static String tr(String key) {
        return LANGUAGE_MANAGER.translate(key);
    }

    private static String trf(String key, Object... args) {
        return String.format(Locale.getDefault(), tr(key), args);
    }

    public static class LanguageManager {
        private final Map<String, LanguagePack> packs = new LinkedHashMap<>();
        private LanguagePack fallbackPack;
        private LanguagePack activePack;

        public synchronized void loadLanguagePacksFromDirectory(File directory) throws IOException {
            if (directory == null || !directory.exists() || !directory.isDirectory()) return;
            File[] files = directory.listFiles((dir, name) -> name.endsWith("LangPack.json"));
            if (files == null) return;
            for (File file : files) {
                loadLanguagePack(file);
            }
        }

        public synchronized boolean loadLanguagePack(File file) throws IOException {
            if (file == null || !file.isFile()) return false;
            String json = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            Map<String, String> map = SimpleJsonParser.parse(json);
            if (map.isEmpty()) return false;
            String code = map.getOrDefault("languageCode", file.getName());
            String name = map.getOrDefault("languageName", code);
            Map<String, String> translations = new LinkedHashMap<>(map);
            translations.remove("languageCode");
            translations.remove("languageName");
            LanguagePack pack = new LanguagePack(code, name, translations, file);
            LanguagePack previous = packs.put(code, pack);
            if (fallbackPack == null || (previous != null && fallbackPack != null && fallbackPack.code.equals(code))) {
                fallbackPack = pack;
            }
            if (activePack == null || (previous != null && activePack != null && activePack.code.equals(code))) {
                activePack = pack;
            }
            return previous == null;
        }

        public synchronized void setFallbackLanguage(String code) {
            if (code == null) return;
            LanguagePack pack = packs.get(code);
            if (pack != null) {
                fallbackPack = pack;
            }
        }

        public synchronized boolean setActiveLanguage(String code) {
            if (code == null) return false;
            LanguagePack pack = packs.get(code);
            if (pack != null) {
                activePack = pack;
                return true;
            }
            return false;
        }

        public synchronized String translate(String key) {
            if (key == null) return "";
            if (activePack != null && activePack.translations.containsKey(key)) {
                return activePack.translations.get(key);
            }
            if (fallbackPack != null && fallbackPack.translations.containsKey(key)) {
                return fallbackPack.translations.get(key);
            }
            return key;
        }

        public synchronized List<LanguagePack> getAvailablePacks() {
            return new ArrayList<>(packs.values());
        }

        public synchronized LanguagePack getActivePack() {
            return activePack;
        }

        public synchronized boolean containsPackFromFile(File file) {
            if (file == null) return false;
            for (LanguagePack pack : packs.values()) {
                if (pack.sourceFile != null && pack.sourceFile.equals(file)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class LanguagePack {
        public final String code;
        public final String name;
        public final Map<String, String> translations;
        public final File sourceFile;

        public LanguagePack(String code, String name, Map<String, String> translations, File sourceFile) {
            this.code = code;
            this.name = name;
            this.translations = translations;
            this.sourceFile = sourceFile;
        }
    }

    public static class SimpleJsonParser {
        public static Map<String, String> parse(String json) throws IOException {
            Map<String, String> map = new LinkedHashMap<>();
            if (json == null) return map;
            int[] idx = new int[]{0};
            skipWhitespace(json, idx);
            if (!expect(json, idx, '{')) {
                throw new IOException("Invalid JSON: expected '{'");
            }
            skipWhitespace(json, idx);
            while (idx[0] < json.length()) {
                if (peek(json, idx) == '}') {
                    idx[0]++;
                    break;
                }
                String key = parseString(json, idx);
                skipWhitespace(json, idx);
                if (!expect(json, idx, ':')) {
                    throw new IOException("Invalid JSON: expected ':'");
                }
                skipWhitespace(json, idx);
                String value = parseValue(json, idx);
                map.put(key, value);
                skipWhitespace(json, idx);
                char ch = peek(json, idx);
                if (ch == ',') {
                    idx[0]++;
                    skipWhitespace(json, idx);
                } else if (ch == '}') {
                    idx[0]++;
                    break;
                } else {
                    throw new IOException("Invalid JSON: expected ',' or '}'");
                }
            }
            return map;
        }

        private static char peek(String json, int[] idx) {
            if (idx[0] >= json.length()) return '\0';
            return json.charAt(idx[0]);
        }

        private static void skipWhitespace(String json, int[] idx) {
            while (idx[0] < json.length() && Character.isWhitespace(json.charAt(idx[0]))) {
                idx[0]++;
            }
        }

        private static boolean expect(String json, int[] idx, char expected) {
            if (idx[0] < json.length() && json.charAt(idx[0]) == expected) {
                idx[0]++;
                return true;
            }
            return false;
        }

        private static String parseString(String json, int[] idx) throws IOException {
            if (!expect(json, idx, '"')) {
                throw new IOException("Invalid JSON: expected string");
            }
            StringBuilder sb = new StringBuilder();
            while (idx[0] < json.length()) {
                char c = json.charAt(idx[0]++);
                if (c == '"') {
                    return sb.toString();
                }
                if (c == '\\') {
                    if (idx[0] >= json.length()) {
                        throw new IOException("Invalid JSON escape sequence");
                    }
                    char esc = json.charAt(idx[0]++);
                    switch (esc) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            if (idx[0] + 4 > json.length()) {
                                throw new IOException("Invalid JSON unicode escape");
                            }
                            String hex = json.substring(idx[0], idx[0] + 4);
                            try {
                                int code = Integer.parseInt(hex, 16);
                                sb.append((char) code);
                            } catch (NumberFormatException ex) {
                                throw new IOException("Invalid JSON unicode escape", ex);
                            }
                            idx[0] += 4;
                            break;
                        default:
                            throw new IOException("Invalid JSON escape character: " + esc);
                    }
                } else {
                    sb.append(c);
                }
            }
            throw new IOException("Unterminated string in JSON");
        }

        private static String parseValue(String json, int[] idx) throws IOException {
            char ch = peek(json, idx);
            if (ch == '"') {
                return parseString(json, idx);
            }
            if (ch == 'n' && json.startsWith("null", idx[0])) {
                idx[0] += 4;
                return "";
            }
            if (ch == 't' && json.startsWith("true", idx[0])) {
                idx[0] += 4;
                return "true";
            }
            if (ch == 'f' && json.startsWith("false", idx[0])) {
                idx[0] += 5;
                return "false";
            }
            StringBuilder sb = new StringBuilder();
            while (idx[0] < json.length()) {
                ch = json.charAt(idx[0]);
                if (ch == ',' || ch == '}' || Character.isWhitespace(ch)) {
                    break;
                }
                sb.append(ch);
                idx[0]++;
            }
            return sb.toString().trim();
        }
    }

    // ---- Model ----
    public enum FieldType implements Serializable { TEXT, LONG_TEXT, NUMBER, BOOLEAN, DATE, LIST, ENUM, IMAGE_PATH }

    public static class EmbeddedResource implements Serializable {
        private static final long serialVersionUID = 1L;
        public String fileName;
        public String mimeType;
        public String base64Data;

        public EmbeddedResource() {}

        public EmbeddedResource(String fileName, String mimeType, String base64Data) {
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.base64Data = base64Data;
        }

        public byte[] decode() {
            if (base64Data == null) return new byte[0];
            return Base64.getDecoder().decode(base64Data);
        }

        @Override
        public String toString() {
            return fileName == null ? "[embedded]" : fileName;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof EmbeddedResource)) return false;
            EmbeddedResource other = (EmbeddedResource) obj;
            return Objects.equals(fileName, other.fileName) && Objects.equals(base64Data, other.base64Data) && Objects.equals(mimeType, other.mimeType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fileName, mimeType, base64Data);
        }
    }

    public static class FieldDefinition implements Serializable {
        public String name;
        public FieldType type;
        public List<String> enumOptions; // only for ENUM
        public String description;
        public Object defaultValue;

        public FieldDefinition() {}

        public FieldDefinition(String name, FieldType type) {
            this.name = name; this.type = type;
        }

        public String toString() { return name + " (" + type + ")"; }
    }

    public static class Schema implements Serializable {
        public List<FieldDefinition> fields = new ArrayList<>();

        public void addField(FieldDefinition fd) {
            if (getField(fd.name) != null) throw new IllegalArgumentException("Field already exists: " + fd.name);
            fields.add(fd);
        }
        public void removeField(String name) {
            fields.removeIf(f -> f.name.equals(name));
        }
        public FieldDefinition getField(String name) {
            for (FieldDefinition f : fields) if (f.name.equals(name)) return f; return null;
        }
        public List<String> fieldNames() { return fields.stream().map(f -> f.name).collect(Collectors.toList()); }

        public void moveField(int from, int to) {
            if (from < 0 || from >= fields.size()) return;
            if (to < 0 || to >= fields.size()) return;
            if (from == to) return;
            FieldDefinition f = fields.remove(from);
            fields.add(to, f);
        }

        public Schema deepCopy() {
            Schema copy = new Schema();
            for (FieldDefinition f : fields) {
                FieldDefinition nf = new FieldDefinition();
                nf.name = f.name;
                nf.type = f.type;
                nf.description = f.description;
                nf.defaultValue = f.defaultValue;
                if (f.enumOptions != null) {
                    nf.enumOptions = new ArrayList<>(f.enumOptions);
                }
                copy.fields.add(nf);
            }
            return copy;
        }
    }

    public static class PersonRecord implements Serializable {
        public UUID id = UUID.randomUUID();
        public Map<String, Object> data = new LinkedHashMap<>();
        public Date createdAt = new Date();
        public Date updatedAt = new Date();

        public String displayName(Schema schema) {
            Object v = data.getOrDefault("이름", "");
            String name = v == null ? "" : String.valueOf(v);
            if (name.isBlank()) return tr("(No Name)");
            return name;
        }
    }

    public static class PersonalDatabase implements Serializable {
        public Schema schema = new Schema();
        public List<PersonRecord> people = new ArrayList<>();

        public PersonRecord addPerson() {
            PersonRecord p = new PersonRecord();
            // initialize blanks
            for (FieldDefinition f : schema.fields) {
                if (!p.data.containsKey(f.name)) p.data.put(f.name, nullDefault(f));
            }
            people.add(p);
            return p;
        }
        public void deletePerson(PersonRecord p) { people.remove(p); }

        public void addFieldToSchema(FieldDefinition fd) {
            schema.addField(fd);
            // backfill all records with blank/default
            for (PersonRecord p : people) {
                p.data.putIfAbsent(fd.name, nullDefault(fd));
                p.updatedAt = new Date();
            }
        }

        private Object nullDefault(FieldDefinition fd) {
            if (fd.defaultValue != null) return fd.defaultValue;
            switch (fd.type) {
                case LIST: return new ArrayList<String>();
                case BOOLEAN: return Boolean.FALSE;
                case NUMBER: return null; // keep null unless provided
                default: return null; // blank
            }
        }

        // --- Search helpers ---
        public List<PersonRecord> searchByFieldValue(String fieldName, String query, boolean partial) {
            FieldDefinition fd = schema.getField(fieldName);
            if (fd == null) return Collections.emptyList();
            List<PersonRecord> res = new ArrayList<>();
            for (PersonRecord p : people) {
                Object v = p.data.get(fieldName);
                if (v == null) continue;
                if (fd.type == FieldType.LIST && v instanceof List) {
                    @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) v;
                    for (Object item : lst) {
                        if (match(String.valueOf(item), query, partial)) { res.add(p); break; }
                    }
                } else {
                    if (match(String.valueOf(v), query, partial)) res.add(p);
                }
            }
            return res;
        }
        private boolean match(String value, String q, boolean partial) {
            if (q == null || q.isBlank()) return false;
            return partial ? value.toLowerCase().contains(q.toLowerCase()) : value.equals(q);
        }

        public Map<String, CompareResult> comparePeople(PersonRecord a, PersonRecord b) {
            Map<String, CompareResult> out = new LinkedHashMap<>();
            for (FieldDefinition f : schema.fields) {
                Object va = a.data.get(f.name);
                Object vb = b.data.get(f.name);
                CompareResult r = new CompareResult(f.name, f.type, va, vb);
                out.put(f.name, r);
            }
            return out;
        }

        // --- Export ---
        public void exportAllToJson(File file) throws IOException {
            String json = JsonUtil.toJson(this);
            Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
        }
        public void exportOneToJson(PersonRecord p, File file) throws IOException {
            String json = JsonUtil.personToJson(p, schema);
            Files.writeString(file.toPath(), json, StandardCharsets.UTF_8);
        }
        public void exportAllToCsv(File file) throws IOException {
            String csv = CsvUtil.toCsv(this);
            Files.writeString(file.toPath(), csv, StandardCharsets.UTF_8);
        }

        public void applySchema(Schema newSchema) {
            Schema copy = newSchema.deepCopy();
            this.schema.fields.clear();
            this.schema.fields.addAll(copy.fields);
            for (PersonRecord p : people) {
                Map<String, Object> updated = new LinkedHashMap<>();
                for (FieldDefinition f : this.schema.fields) {
                    Object val = p.data.get(f.name);
                    if (val == null) {
                        val = nullDefault(f);
                    }
                    updated.put(f.name, val);
                }
                p.data = updated;
                p.updatedAt = new Date();
            }
        }

        public Schema snapshotSchema() {
            return schema.deepCopy();
        }
    }

    public static class AppSettings implements Serializable {
        private static final long serialVersionUID = 1L;
        private String dataDirectory = DEFAULT_DATA_DIRECTORY;
        private String languageCode = "en_US";

        public String getDataDirectory() {
            return dataDirectory;
        }

        public File getDataDirectoryFile() {
            return new File(dataDirectory);
        }

        public void setDataDirectory(String path) {
            if (path == null || path.isBlank()) return;
            this.dataDirectory = path;
            ensureDataDirectoryExists();
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            if (languageCode == null || languageCode.isBlank()) return;
            this.languageCode = languageCode;
        }

        public void ensureDataDirectoryExists() {
            File dir = getDataDirectoryFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        public void applyFrom(AppSettings other) {
            if (other == null) return;
            setDataDirectory(other.getDataDirectory());
            setLanguageCode(other.getLanguageCode());
        }
    }

    public static class AppState implements Serializable {
        private static final long serialVersionUID = 1L;
        public PersonalDatabase database;
        public AppSettings settings;

        public AppState() {}

        public AppState(PersonalDatabase db, AppSettings settings) {
            this.database = db;
            this.settings = settings;
        }
    }

    public static class SchemaIO {
        public static void saveSchema(Schema schema, File file) throws IOException {
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(schema);
            }
        }

        public static Schema loadSchema(File file) throws IOException, ClassNotFoundException {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Schema schema = (Schema) ois.readObject();
                return schema == null ? new Schema() : schema;
            }
        }
    }

    public static class CompareResult implements Serializable {
        public final String fieldName;
        public final FieldType type;
        public final Object aValue;
        public final Object bValue;
        public final boolean same;
        public final String overlap; // for LIST: overlapping items
        public CompareResult(String fieldName, FieldType type, Object aValue, Object bValue) {
            this.fieldName = fieldName; this.type = type; this.aValue = aValue; this.bValue = bValue;
            if (type == FieldType.LIST) {
                Set<String> sa = toSet(aValue);
                Set<String> sb = toSet(bValue);
                Set<String> inter = new LinkedHashSet<>(sa);
                inter.retainAll(sb);
                this.overlap = String.join("; ", inter);
                this.same = sa.equals(sb);
            } else {
                this.overlap = "";
                this.same = Objects.equals(normalize(aValue), normalize(bValue));
            }
        }
        private static Set<String> toSet(Object v) {
            if (v instanceof List) {
                @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) v;
                return lst.stream().map(x -> x==null?"":String.valueOf(x)).collect(Collectors.toCollection(LinkedHashSet::new));
            }
            if (v == null) return Collections.emptySet();
            return new LinkedHashSet<>(List.of(String.valueOf(v)));
        }
        private static Object normalize(Object v) {
            if (v == null) return null;
            return v;
        }
    }

    // ---- Utility: JSON (writer only, no parser needed) ----
    public static class JsonUtil {
        private static final SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        public static String toJson(PersonalDatabase db) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");
            // schema
            sb.append("  \"schema\": {");
            sb.append("\n    \"fields\": [\n");
            for (int i = 0; i < db.schema.fields.size(); i++) {
                FieldDefinition f = db.schema.fields.get(i);
                sb.append("      {");
                sb.append("\"name\": \"").append(esc(f.name)).append("\",");
                sb.append(" \"type\": \"").append(esc(f.type.name())).append("\"");
                if (f.description != null) sb.append(", \"description\": \"").append(esc(f.description)).append("\"");
                if (f.type == FieldType.ENUM && f.enumOptions != null) {
                    sb.append(", \"enumOptions\": [");
                    for (int j = 0; j < f.enumOptions.size(); j++) {
                        if (j>0) sb.append(", ");
                        sb.append("\"").append(esc(f.enumOptions.get(j))).append("\"");
                    }
                    sb.append("]");
                }
                sb.append("}");
                if (i < db.schema.fields.size()-1) sb.append(",");
                sb.append("\n");
            }
            sb.append("    ]\n  },\n");
            // people
            sb.append("  \"people\": [\n");
            for (int i = 0; i < db.people.size(); i++) {
                PersonRecord p = db.people.get(i);
                sb.append(personJsonObject(p, db.schema));
                if (i < db.people.size()-1) sb.append(",");
                sb.append("\n");
            }
            sb.append("  ]\n}");
            return sb.toString();
        }
        public static String personToJson(PersonRecord p, Schema s) {
            StringBuilder sb = new StringBuilder();
            sb.append(personJsonObject(p, s));
            return sb.toString();
        }
        private static String personJsonObject(PersonRecord p, Schema s) {
            StringBuilder sb = new StringBuilder();
            sb.append("    {\n");
            sb.append("      \"id\": \"").append(p.id.toString()).append("\",\n");
            sb.append("      \"createdAt\": \"").append(esc(ISO.format(p.createdAt))).append("\",\n");
            sb.append("      \"updatedAt\": \"").append(esc(ISO.format(p.updatedAt))).append("\",\n");
            sb.append("      \"data\": {\n");
            for (int i = 0; i < s.fields.size(); i++) {
                FieldDefinition f = s.fields.get(i);
                sb.append("        \"").append(esc(f.name)).append("\": ");
                sb.append(valueToJson(p.data.get(f.name), f.type));
                if (i < s.fields.size()-1) sb.append(",");
                sb.append("\n");
            }
            sb.append("      }\n    }");
            return sb.toString();
        }
        private static String valueToJson(Object v, FieldType t) {
            if (v == null) return "null";
            switch (t) {
                case BOOLEAN: return Boolean.TRUE.equals(v) ? "true" : "false";
                case NUMBER:
                    if (v instanceof Number) return v.toString(); else return quote(String.valueOf(v));
                case LIST:
                    if (v instanceof List) {
                        @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) v;
                        StringBuilder sb = new StringBuilder("[");
                        for (int i = 0; i < lst.size(); i++) {
                            if (i>0) sb.append(", ");
                            sb.append(quote(String.valueOf(lst.get(i))));
                        }
                        sb.append("]");
                        return sb.toString();
                    } else return "[]";
                case IMAGE_PATH:
                    if (v instanceof EmbeddedResource) {
                        EmbeddedResource res = (EmbeddedResource) v;
                        StringBuilder img = new StringBuilder();
                        img.append("{");
                        img.append("\"fileName\": ").append(res.fileName == null ? "null" : quote(res.fileName));
                        img.append(", \"mimeType\": ").append(res.mimeType == null ? "null" : quote(res.mimeType));
                        img.append(", \"base64\": ").append(res.base64Data == null ? "null" : quote(res.base64Data));
                        img.append("}");
                        return img.toString();
                    }
                    if (v == null) return "null";
                    return quote(String.valueOf(v));
                default:
                    return quote(String.valueOf(v));
            }
        }
        private static String quote(String s) { return "\"" + esc(s) + "\""; }
        private static String esc(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
        }
    }

    // ---- Utility: CSV (flat) ----
    public static class CsvUtil {
        public static String toCsv(PersonalDatabase db) {
            StringBuilder sb = new StringBuilder();
            // header
            sb.append("id,createdAt,updatedAt");
            for (FieldDefinition f : db.schema.fields) {
                sb.append(",").append(escapeCsv(f.name));
            }
            sb.append("\n");
            // rows
            SimpleDateFormat ISO = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            for (PersonRecord p : db.people) {
                sb.append(p.id.toString()).append(",");
                sb.append(escapeCsv(ISO.format(p.createdAt))).append(",");
                sb.append(escapeCsv(ISO.format(p.updatedAt)));
                for (FieldDefinition f : db.schema.fields) {
                    sb.append(",");
                    Object v = p.data.get(f.name);
                    if (v == null) { sb.append(""); continue; }
                    if (f.type == FieldType.LIST && v instanceof List) {
                        @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) v;
                        String joined = lst.stream().map(x -> x==null?"":String.valueOf(x)).collect(Collectors.joining("; "));
                        sb.append(escapeCsv(joined));
                    } else if (f.type == FieldType.IMAGE_PATH && v instanceof EmbeddedResource) {
                        EmbeddedResource res = (EmbeddedResource) v;
                        String label = res.fileName != null ? res.fileName : "[embedded]";
                        sb.append(escapeCsv(label));
                    } else {
                        sb.append(escapeCsv(String.valueOf(v)));
                    }
                }
                sb.append("\n");
            }
            return sb.toString();
        }
        private static String escapeCsv(String s) {
            if (s == null) return "";
            boolean needsQuote = s.contains(",") || s.contains("\n") || s.contains("\"");
            String t = s.replace("\"", "\"\"");
            return needsQuote ? "\"" + t + "\"" : t;
        }
    }

    // ---- UI components ----
    private final PersonalDatabase db;
    private final AppSettings settings;
    private final DefaultListModel<PersonRecord> peopleListModel = new DefaultListModel<>();
    private final JList<PersonRecord> peopleList = new JList<>(peopleListModel);
    private final JPanel detailsForm = new JPanel(new GridBagLayout());
    private final JScrollPane detailsScroll = new JScrollPane(detailsForm);
    private final JComboBox<String> searchFieldCombo = new JComboBox<>();
    private final JTextField searchValueField = new JTextField();
    private final DefaultListModel<PersonRecord> searchResultsModel = new DefaultListModel<>();
    private final JList<PersonRecord> searchResultsList = new JList<>(searchResultsModel);
    private final JComboBox<PersonRecord> compareA = new JComboBox<>();
    private final JComboBox<PersonRecord> compareB = new JComboBox<>();
    private final JTextArea compareOut = new JTextArea(10, 50);
    private final JTable schemaTable = new JTable();
    private final SchemaTableModel schemaTableModel;

    private JMenu languageMenu;
    private ButtonGroup languageMenuGroup;
    private WatchService languageWatchService;
    private Thread languageWatchThread;

    private TrayIcon trayIcon;

    private File currentProjectFile = null; // .pdb serialized
    private File defaultProjectFile;
    private File defaultSchemaFile;
    private File lockedProjectFile;
    private File lockedSchemaFile;
    private RandomAccessFile projectLockRaf;
    private FileLock projectFileLock;
    private RandomAccessFile schemaLockRaf;
    private FileLock schemaFileLock;
    private javax.swing.Timer autoSaveTimer;
    private Map<String, JComponent> currentEditors = new HashMap<>();
    private PersonRecord currentPerson;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private boolean restartScheduled = false;

    public V1_4_0___PersonalDBApp() {
        super(tr("PersonalDB"));
        this.db = new PersonalDatabase();
        this.settings = new AppSettings();
        this.defaultProjectFile = new File(settings.getDataDirectoryFile(), DEFAULT_PROJECT_FILE_NAME);
        this.defaultSchemaFile = new File(DEFAULT_CONFIG_DIRECTORY, DEFAULT_SCHEMA_FILE_NAME);
        LanguagePack active = LANGUAGE_MANAGER.getActivePack();
        if (active != null) {
            this.settings.setLanguageCode(active.code);
        }
        seedInitialSchema(db.schema);
        schemaTableModel = new SchemaTableModel();
        buildUI();
        configureWindowCloseBehavior();
        setupGlobalKeyBindings();
        refreshPeopleList();
        SwingUtilities.invokeLater(this::postStartupInitialization);
        setTitle(tr("PersonalDB"));

        WindowAdapter initialResizeListener = new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                forceInitialResizeWorkaround();
                V1_4_0___PersonalDBApp.this.removeWindowListener(this);
            }
        };
        addWindowListener(initialResizeListener);
    }

    private void forceInitialResizeWorkaround() {
        final int originalState = getExtendedState();
        final Dimension originalSize = getSize();
        final Point originalLocation = getLocation();

        SwingUtilities.invokeLater(() -> {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            SwingUtilities.invokeLater(() -> {
                setExtendedState(originalState);
                if ((originalState & JFrame.MAXIMIZED_BOTH) == 0) {
                    setSize(originalSize);
                    setLocation(originalLocation);
                }
            });
        });
    }

    private void postStartupInitialization() {
        settings.ensureDataDirectoryExists();
        ensureConfigDirectoryExists();
        defaultProjectFile = new File(settings.getDataDirectoryFile(), DEFAULT_PROJECT_FILE_NAME);
        defaultSchemaFile = new File(DEFAULT_CONFIG_DIRECTORY, DEFAULT_SCHEMA_FILE_NAME);
        initializeDefaultFiles();
        initializeTrayIcon();
        syncLanguageFromSettings();
        startLanguagePackWatcher();
        File globalSchemaFile = new File(DEFAULT_CONFIG_DIRECTORY, GLOBAL_SCHEMA_FILE_NAME);
        if (!loadGlobalSchemaFromConfig()) {
            int choice = JOptionPane.showConfirmDialog(this,
                    tr("Global schema file is missing. Load it now? (requires restart)"),
                    tr("Global Schema"), JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                if (importGlobalSchemaIntoConfig(globalSchemaFile)) {
                    JOptionPane.showMessageDialog(this, tr("Global schema copied into the config folder. The app will restart."));
                    scheduleRestart();
                }
            }
        }
        startAutoSaveTimer();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopLanguagePackWatcher();
            releaseLocks();
            removeTrayIcon();
        }, "PersonalDBShutdown"));
    }

    private void ensureConfigDirectoryExists() {
        ensureDefaultConfigDirectory();
    }

    private boolean importGlobalSchemaIntoConfig(File destination) {
        JFileChooser fc = new JFileChooser(settings.getDataDirectoryFile());
        fc.setFileFilter(new FileNameExtensionFilter(tr("PersonalDB Schema (*.psc)"), "psc"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File selected = fc.getSelectedFile();
        try {
            Schema schema = SchemaIO.loadSchema(selected);
            SchemaIO.saveSchema(schema, destination);
            applySchemaFromExternal(schema);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, trf("Failed to load schema: %s", ex.getMessage()), tr("Error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void scheduleRestart() {
        if (restartScheduled) return;
        restartScheduled = true;
        SwingUtilities.invokeLater(() -> {
            setVisible(false);
            stopAutoSaveTimer();
            stopLanguagePackWatcher();
            removeTrayIcon();
            releaseLocks();
            dispose();
            V1_4_0___PersonalDBApp app = new V1_4_0___PersonalDBApp();
            app.setVisible(true);
        });
    }

    private void applySchemaFromExternal(Schema schema) {
        if (schema == null) return;
        db.applySchema(schema);
        schemaTableModel.fireTableDataChanged();
        refreshSearchFieldCombo();
        buildDetailsForm(peopleList.getSelectedValue());
        peopleList.repaint();
        persistDefaultSchema();
    }

    private boolean loadGlobalSchemaFromConfig() {
        File globalSchemaFile = new File(DEFAULT_CONFIG_DIRECTORY, GLOBAL_SCHEMA_FILE_NAME);
        if (!globalSchemaFile.exists()) return false;
        try {
            Schema schema = SchemaIO.loadSchema(globalSchemaFile);
            applySchemaFromExternal(schema);
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, trf("Failed to load global schema: %s", ex.getMessage()), tr("Error"), JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void importGlobalSchemaFromMenu() {
        ensureConfigDirectoryExists();
        File dest = new File(DEFAULT_CONFIG_DIRECTORY, GLOBAL_SCHEMA_FILE_NAME);
        if (importGlobalSchemaIntoConfig(dest)) {
            int restart = JOptionPane.showConfirmDialog(this,
                    tr("Global schema imported. Restart now?"),
                    tr("Restart"), JOptionPane.YES_NO_OPTION);
            if (restart == JOptionPane.YES_OPTION) {
                scheduleRestart();
            }
        }
    }

    private void exportGlobalSchema() {
        JFileChooser fc = createFileChooser(tr("PersonalDB Schema (*.psc)"), "psc");
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File target = ensureExt(fc.getSelectedFile(), ".psc");
        try {
            SchemaIO.saveSchema(db.snapshotSchema(), target);
            JOptionPane.showMessageDialog(this, trf("Global schema exported: %s", target.getAbsolutePath()));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, trf("Failed to export global schema: %s", ex.getMessage()), tr("Error"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void chooseDataDirectory() {
        JFileChooser fc = new JFileChooser(settings.getDataDirectoryFile());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle(tr("Select Data Directory"));
        if (fc.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File dir = fc.getSelectedFile();
        settings.setDataDirectory(dir.getAbsolutePath());
        JOptionPane.showMessageDialog(this, trf("Data directory changed to: %s", dir.getAbsolutePath()));
    }

    private void seedInitialSchema(Schema s) {
        // Starter fields (can be edited/removed by user later)
        s.addField(fd("사진", FieldType.IMAGE_PATH));
        s.addField(fd("이름", FieldType.TEXT));
        s.addField(fd("만나게 된 계기", FieldType.LONG_TEXT));
        s.addField(fd("전화번호", FieldType.TEXT));
        s.addField(fd("사는곳", FieldType.TEXT));
        s.addField(fd("특성", FieldType.LONG_TEXT));
        s.addField(fd("좋아하는것", FieldType.LIST));
        s.addField(fd("싫어하는것", FieldType.LIST));
        s.addField(fd("생일", FieldType.DATE));
        s.addField(fd("말투", FieldType.TEXT));
        s.addField(fd("성향", FieldType.TEXT));
        s.addField(fd("관심사", FieldType.LIST));
        FieldDefinition mbti = fd("MBTI", FieldType.ENUM);
        mbti.enumOptions = List.of("INTJ","INTP","ENTJ","ENTP","INFJ","INFP","ENFJ","ENFP","ISTJ","ISFJ","ESTJ","ESFJ","ISTP","ISFP","ESTP","ESFP");
        db.schema.addField(mbti);
    }
    private FieldDefinition fd(String name, FieldType t) { return new FieldDefinition(name, t); }

    private void configureWindowCloseBehavior() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                requestExit();
            }
        });
    }

    private void setupGlobalKeyBindings() {
        JRootPane root = getRootPane();
        if (root == null) {
            return;
        }
        int shortcutMask;
        try {
            shortcutMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        } catch (HeadlessException ex) {
            shortcutMask = InputEvent.CTRL_DOWN_MASK;
        }
        KeyStroke saveStroke = KeyStroke.getKeyStroke(KeyEvent.VK_S, shortcutMask);
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(saveStroke, "global-save");
        root.getActionMap().put("global-save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performQuickSave();
            }
        });
    }

    private void buildUI() {
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setSize(1000, 720);
        setLocationRelativeTo(null);

        // Menu
        setJMenuBar(buildMenuBar());

        // Left list
        peopleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peopleList.setCellRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PersonRecord) {
                    PersonRecord p = (PersonRecord) value;
                    lbl.setText(p.displayName(db.schema));
                }
                return lbl;
            }
        });
        peopleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                saveCurrentPerson();
                buildDetailsForm(peopleList.getSelectedValue());
            }
        });
        JButton addBtn = new JButton(tr("+ New Person"));
        addBtn.addActionListener(e -> {
            PersonRecord p = db.addPerson();
            peopleListModel.addElement(p);
            peopleList.setSelectedValue(p, true);
        });
        JButton delBtn = new JButton(tr("Delete"));
        delBtn.addActionListener(e -> {
            PersonRecord sel = peopleList.getSelectedValue();
            if (sel == null) return;
            int c = JOptionPane.showConfirmDialog(this, tr("Are you sure you want to delete this record?"), tr("Confirm"), JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                db.deletePerson(sel);
                peopleListModel.removeElement(sel);
                buildDetailsForm(null);
                refreshCompareCombos();
            }
        });
        JButton loadProjectBtn = new JButton(tr("Load PDB"));
        loadProjectBtn.addActionListener(e -> openProject());
        JButton saveProjectBtn = new JButton(tr("Save PDB"));
        saveProjectBtn.addActionListener(e -> saveProject(false));
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBorder(new EmptyBorder(8, 8, 8, 8));
        JPanel leftButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtons.add(addBtn);
        leftButtons.add(delBtn);
        leftButtons.add(loadProjectBtn);
        leftButtons.add(saveProjectBtn);
        leftHeader.add(leftButtons, BorderLayout.WEST);
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftHeader, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(peopleList), BorderLayout.CENTER);

        // Right tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab(tr("Details"), detailsScroll);
        tabs.addTab(tr("Search / Compare"), buildSearchComparePanel());
        tabs.addTab(tr("Schema"), buildSchemaPanel());

        detailsScroll.getVerticalScrollBar().setUnitIncrement(24);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, tabs);
        split.setDividerLocation(280);
        getContentPane().add(split, BorderLayout.CENTER);

        // Initially no selection
        buildDetailsForm(null);
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu file = new JMenu(tr("File"));
        JMenuItem miNew = new JMenuItem(tr("New Project"));
        miNew.addActionListener(e -> newProject());
        JMenuItem miOpen = new JMenuItem(tr("Open Project (.pdb)"));
        miOpen.addActionListener(e -> openProject());
        JMenuItem miSave = new JMenuItem(tr("Save Project"));
        miSave.addActionListener(e -> saveProject(false));
        JMenuItem miSaveAs = new JMenuItem(tr("Save Project As..."));
        miSaveAs.addActionListener(e -> saveProject(true));
        JMenuItem miExportAllJson = new JMenuItem(tr("Export ALL to JSON..."));
        miExportAllJson.addActionListener(e -> exportAllJson());
        JMenuItem miExportOneJson = new JMenuItem(tr("Export SELECTED to JSON..."));
        miExportOneJson.addActionListener(e -> exportOneJson());
        JMenuItem miExportCsv = new JMenuItem(tr("Export ALL to CSV..."));
        miExportCsv.addActionListener(e -> exportAllCsv());
        JMenuItem miExit = new JMenuItem(tr("Exit"));
        miExit.addActionListener(e -> requestExit());
        file.add(miNew); file.add(miOpen); file.addSeparator(); file.add(miSave); file.add(miSaveAs); file.addSeparator();
        file.add(miExportAllJson); file.add(miExportOneJson); file.add(miExportCsv); file.addSeparator(); file.add(miExit);

        JMenu edit = new JMenu(tr("Edit"));
        JMenuItem miAddField = new JMenuItem(tr("Add Field"));
        miAddField.addActionListener(e -> addFieldDialog());
        JMenuItem miRemoveField = new JMenuItem(tr("Remove Field"));
        miRemoveField.addActionListener(e -> removeFieldDialog());
        edit.add(miAddField); edit.add(miRemoveField);

        JMenu help = new JMenu(tr("Help"));
        JMenuItem miAbout = new JMenuItem(tr("About"));
        miAbout.addActionListener(e -> JOptionPane.showMessageDialog(this,
                tr("Establish your Personal Database! - PersonalDB") + "\n" +
                        tr("• Dynamic custom fields") + "\n" +
                        tr("• Export as JSON / CSV") + "\n" +
                        tr("• Quick search & compare") + "\n\n" +
                        tr("Copyright 2025~sometime in the future") + "\n" +
                        tr("MyungSu(a.k.a. Plutrious, Pigman_MS, Tensor) & Codex"),
                tr("About"), JOptionPane.INFORMATION_MESSAGE));
        help.add(miAbout);

        JMenu config = new JMenu(tr("Config"));
        JMenuItem miSetDataDir = new JMenuItem(tr("Set Data Directory..."));
        miSetDataDir.addActionListener(e -> chooseDataDirectory());
        JMenuItem miImportGlobal = new JMenuItem(tr("Import Global Schema (.psc)..."));
        miImportGlobal.addActionListener(e -> importGlobalSchemaFromMenu());
        JMenuItem miExportGlobal = new JMenuItem(tr("Export Current Schema (.psc)..."));
        miExportGlobal.addActionListener(e -> exportGlobalSchema());
        config.add(miSetDataDir);
        config.add(miImportGlobal);
        config.add(miExportGlobal);

        languageMenu = new JMenu(tr("Language"));
        rebuildLanguageMenuItems();

        mb.add(file); mb.add(edit); mb.add(config); mb.add(languageMenu); mb.add(help);
        return mb;
    }

    private void rebuildLanguageMenuItems() {
        if (languageMenu == null) {
            return;
        }
        languageMenu.setText(tr("Language"));
        languageMenu.removeAll();
        languageMenuGroup = new ButtonGroup();
        LanguagePack active = LANGUAGE_MANAGER.getActivePack();
        for (LanguagePack pack : LANGUAGE_MANAGER.getAvailablePacks()) {
            final LanguagePack targetPack = pack;
            boolean selected = active != null && active.code.equals(targetPack.code);
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(targetPack.name, selected);
            item.addActionListener(e -> {
                if (LANGUAGE_MANAGER.setActiveLanguage(targetPack.code)) {
                    settings.setLanguageCode(targetPack.code);
                    JOptionPane.showMessageDialog(this, trf("Language changed to %s. The application will restart.", targetPack.name));
                    scheduleRestart();
                }
            });
            languageMenuGroup.add(item);
            languageMenu.add(item);
        }
        languageMenu.revalidate();
        languageMenu.repaint();
    }

    private void startLanguagePackWatcher() {
        stopLanguagePackWatcher();
        if (!DEFAULT_CONFIG_DIRECTORY.exists()) {
            return;
        }
        try {
            languageWatchService = FileSystems.getDefault().newWatchService();
            Path path = DEFAULT_CONFIG_DIRECTORY.toPath();
            path.register(languageWatchService, StandardWatchEventKinds.ENTRY_CREATE);
            languageWatchThread = new Thread(this::processLanguageWatchEvents, "LanguagePackWatcher");
            languageWatchThread.setDaemon(true);
            languageWatchThread.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void processLanguageWatchEvents() {
        if (languageWatchService == null) {
            return;
        }
        while (true) {
            WatchKey key;
            try {
                key = languageWatchService.take();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            } catch (ClosedWatchServiceException ex) {
                break;
            }
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind != StandardWatchEventKinds.ENTRY_CREATE) {
                    continue;
                }
                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path filename = ev.context();
                if (filename == null) {
                    continue;
                }
                if (!filename.toString().endsWith("LangPack.json")) {
                    continue;
                }
                Path resolved = DEFAULT_CONFIG_DIRECTORY.toPath().resolve(filename);
                handleLanguagePackFileCreated(resolved.toFile());
            }
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
    }

    private void handleLanguagePackFileCreated(File file) {
        if (file == null) {
            return;
        }
        try {
            Thread.sleep(200);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            return;
        }
        boolean loaded;
        try {
            LANGUAGE_MANAGER.loadLanguagePack(file);
            loaded = LANGUAGE_MANAGER.containsPackFromFile(file);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if (!loaded) {
            return;
        }
        SwingUtilities.invokeLater(() -> {
            rebuildLanguageMenuItems();
            JOptionPane.showMessageDialog(V1_4_0___PersonalDBApp.this, "새로운 언어팩이 감지되었습니다.");
        });
    }

    private void stopLanguagePackWatcher() {
        if (languageWatchThread != null) {
            languageWatchThread.interrupt();
            languageWatchThread = null;
        }
        if (languageWatchService != null) {
            try {
                languageWatchService.close();
            } catch (IOException ignored) {
            }
            languageWatchService = null;
        }
    }

    private void syncLanguageFromSettings() {
        String code = settings.getLanguageCode();
        if (!LANGUAGE_MANAGER.setActiveLanguage(code)) {
            LANGUAGE_MANAGER.setActiveLanguage("en_US");
            settings.setLanguageCode("en_US");
        }
        rebuildLanguageMenuItems();
    }

    @Override
    public void dispose() {
        stopAutoSaveTimer();
        stopLanguagePackWatcher();
        removeTrayIcon();
        releaseLocks();
        super.dispose();
    }

    private JPanel buildSearchComparePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new GridBagLayout());
        top.setBorder(new EmptyBorder(8,8,8,8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4,4,4,4);
        gc.gridx=0; gc.gridy=0; gc.anchor=GridBagConstraints.WEST;
        top.add(new JLabel(tr("Field")), gc);
        gc.gridx=1; gc.weightx=1; gc.fill=GridBagConstraints.HORIZONTAL;
        searchFieldCombo.setPrototypeDisplayValue(tr("Interests................................"));
        refreshSearchFieldCombo();
        searchFieldCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof String) {
                    lbl.setText(tr((String) value));
                }
                return lbl;
            }
        });
        top.add(searchFieldCombo, gc);
        gc.gridx=0; gc.gridy=1; gc.weightx=0; gc.fill=GridBagConstraints.NONE;
        top.add(new JLabel(tr("Value")), gc);
        gc.gridx=1; gc.weightx=1; gc.fill=GridBagConstraints.HORIZONTAL;
        top.add(searchValueField, gc);
        gc.gridx=2; gc.weightx=0; gc.fill=GridBagConstraints.NONE;
        JButton btnSearch = new JButton(tr("Search"));
        btnSearch.addActionListener(e -> doSearch());
        top.add(btnSearch, gc);

        JPanel center = new JPanel(new BorderLayout());
        searchResultsList.setCellRenderer(new DefaultListCellRenderer(){
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PersonRecord) {
                    lbl.setText(((PersonRecord) value).displayName(db.schema));
                }
                return lbl;
            }
        });
        center.add(new JScrollPane(searchResultsList), BorderLayout.CENTER);

        JPanel comparePanel = new JPanel(new GridBagLayout());
        comparePanel.setBorder(BorderFactory.createTitledBorder(tr("Compare two people")));
        GridBagConstraints c2 = new GridBagConstraints(); c2.insets = new Insets(4,4,4,4);
        c2.gridx=0; c2.gridy=0; comparePanel.add(new JLabel(tr("A:")), c2);
        c2.gridx=1; c2.weightx=1; c2.fill=GridBagConstraints.HORIZONTAL; refreshCompareCombos(); comparePanel.add(compareA, c2);
        c2.gridx=0; c2.gridy=1; c2.weightx=0; c2.fill=GridBagConstraints.NONE; comparePanel.add(new JLabel(tr("B:")), c2);
        c2.gridx=1; c2.weightx=1; c2.fill=GridBagConstraints.HORIZONTAL; comparePanel.add(compareB, c2);
        JButton btnCompare = new JButton(tr("Compare"));
        btnCompare.addActionListener(e -> doCompare());
        c2.gridx=2; c2.gridy=0; c2.gridheight=2; c2.fill=GridBagConstraints.VERTICAL; comparePanel.add(btnCompare, c2);

        compareOut.setEditable(false);
        compareOut.setLineWrap(true);
        compareOut.setWrapStyleWord(true);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(comparePanel, BorderLayout.NORTH);
        bottom.add(new JScrollPane(compareOut), BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSchemaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        schemaTable.setModel(schemaTableModel);
        panel.add(new JScrollPane(schemaTable), BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton(tr("Add Field")); add.addActionListener(e -> addFieldDialog());
        JButton remove = new JButton(tr("Remove Field")); remove.addActionListener(e -> removeFieldDialog());
        JButton up = new JButton(tr("Move Up")); up.addActionListener(e -> moveSelectedField(-1));
        JButton down = new JButton(tr("Move Down")); down.addActionListener(e -> moveSelectedField(1));
        btns.add(add); btns.add(remove);
        btns.add(up); btns.add(down);
        panel.add(btns, BorderLayout.NORTH);
        return panel;
    }

    private void moveSelectedField(int delta) {
        int row = schemaTable.getSelectedRow();
        if (row < 0) return;
        int target = row + delta;
        if (target < 0 || target >= db.schema.fields.size()) return;
        db.schema.moveField(row, target);
        schemaTableModel.fireTableDataChanged();
        schemaTable.setRowSelectionInterval(target, target);
        refreshSearchFieldCombo();
        buildDetailsForm(peopleList.getSelectedValue());
    }

    private void refreshSearchFieldCombo() {
        searchFieldCombo.removeAllItems();
        for (FieldDefinition f : db.schema.fields) searchFieldCombo.addItem(f.name);
    }

    private void refreshCompareCombos() {
        compareA.removeAllItems();
        compareB.removeAllItems();
        for (int i=0;i<peopleListModel.getSize();i++) {
            PersonRecord p = peopleListModel.get(i);
            compareA.addItem(p);
            compareB.addItem(p);
        }
        compareA.setRenderer(new PersonComboRenderer());
        compareB.setRenderer(new PersonComboRenderer());
    }

    class PersonComboRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof PersonRecord) lbl.setText(((PersonRecord) value).displayName(db.schema));
            return lbl;
        }
    }

    private void doSearch() {
        String field = (String) searchFieldCombo.getSelectedItem();
        String val = searchValueField.getText();
        searchResultsModel.clear();
        if (field == null || val.isBlank()) return;
        List<PersonRecord> hits = db.searchByFieldValue(field, val, true);
        for (PersonRecord p : hits) searchResultsModel.addElement(p);
    }

    private void doCompare() {
        PersonRecord a = (PersonRecord) compareA.getSelectedItem();
        PersonRecord b = (PersonRecord) compareB.getSelectedItem();
        if (a == null || b == null || a == b) { compareOut.setText(tr("Select two different people to compare.")); return; }
        Map<String, CompareResult> cmp = db.comparePeople(a, b);
        StringBuilder sb = new StringBuilder();
        sb.append(tr("[Common / Same Attributes]")); sb.append("\n");
        for (CompareResult r : cmp.values()) {
            if (r.type == FieldType.LIST) {
                if (!r.overlap.isBlank()) sb.append(" • ").append(tr(r.fieldName)).append(": ").append(tr("[Overlap]"))
                        .append(" ").append(r.overlap).append("\n");
            } else if (r.same && r.aValue != null && !String.valueOf(r.aValue).isBlank()) {
                sb.append(" • ").append(tr(r.fieldName)).append(": ").append(r.aValue).append("\n");
            }
        }
        sb.append("\n").append(tr("[Differences]")); sb.append("\n");
        for (CompareResult r : cmp.values()) {
            if (r.type == FieldType.LIST) {
                // Show A-only / B-only
                Set<String> sa = r.aValue instanceof List ? new LinkedHashSet<>((List<String>) r.aValue) : Collections.emptySet();
                Set<String> sbB = r.bValue instanceof List ? new LinkedHashSet<>((List<String>) r.bValue) : Collections.emptySet();
                Set<String> onlyA = new LinkedHashSet<>(sa); onlyA.removeAll(sbB);
                Set<String> onlyB = new LinkedHashSet<>(sbB); onlyB.removeAll(sa);
                if (!onlyA.isEmpty() || !onlyB.isEmpty()) {
                    sb.append(" • ").append(tr(r.fieldName)).append(": ")
                            .append(tr("A"))
                            .append("[").append(String.join("; ", onlyA)).append("] ")
                            .append(tr("vs"))
                            .append(" ")
                            .append(tr("B"))
                            .append("[").append(String.join("; ", onlyB)).append("]\n");
                }
            } else {
                String va = r.aValue==null?"":String.valueOf(r.aValue);
                String vb = r.bValue==null?"":String.valueOf(r.bValue);
                if (!Objects.equals(va, vb) && (!va.isBlank() || !vb.isBlank())) {
                    sb.append(" • ").append(tr(r.fieldName)).append(": ")
                            .append(tr("A"))
                            .append("[").append(va).append("] ")
                            .append(tr("vs"))
                            .append(" ")
                            .append(tr("B"))
                            .append("[").append(vb).append("]\n");
                }
            }
        }
        compareOut.setText(sb.toString());
    }

    private void refreshPeopleList() {
        peopleListModel.clear();
        for (PersonRecord p : db.people) peopleListModel.addElement(p);
        refreshSearchFieldCombo();
        refreshCompareCombos();
    }

    // Build details form dynamically per schema
    private void buildDetailsForm(PersonRecord p) {
        detailsForm.removeAll();
        detailsForm.setBorder(new EmptyBorder(8,8,8,8));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        int row = 0;
        currentEditors = new LinkedHashMap<>();
        currentPerson = p;

        for (FieldDefinition f : db.schema.fields) {
            gc.gridx = 0; gc.gridy = row; gc.weightx=0; gc.fill=GridBagConstraints.NONE;
            JLabel label = new JLabel(tr(f.name));
            detailsForm.add(label, gc);

            gc.gridx = 1; gc.gridy = row; gc.weightx=1; gc.fill=GridBagConstraints.HORIZONTAL;
            JComponent editor = editorForField(f, p==null?null:p.data.get(f.name));
            detailsForm.add(editor, gc);
            currentEditors.put(f.name, editor);
            row++;
            if (f.type == FieldType.LONG_TEXT) {
                gc.gridx = 1; gc.gridy = row-1; gc.weighty=1; gc.fill=GridBagConstraints.BOTH;
            }
        }

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton(tr("Save (selected person)"));
        saveBtn.addActionListener(e -> saveCurrentPerson());
        JButton exportOne = new JButton(tr("Export (JSON; selected person)"));
        exportOne.addActionListener(e -> exportOneJson());
        btns.add(saveBtn); btns.add(exportOne);

        gc.gridx=0; gc.gridy=row; gc.gridwidth=2; gc.weighty=0; gc.fill=GridBagConstraints.HORIZONTAL;
        detailsForm.add(btns, gc);

        detailsForm.revalidate();
        detailsForm.repaint();
    }

    private void applyEditorsToRecord(PersonRecord p, Map<String, JComponent> editors) {
        for (FieldDefinition f : db.schema.fields) {
            JComponent ed = editors.get(f.name);
            Object value = readEditorValue(f, ed);
            p.data.put(f.name, value);
        }
    }

    private void saveCurrentPerson() {
        if (currentPerson == null || currentEditors == null || currentEditors.isEmpty()) {
            return;
        }
        applyEditorsToRecord(currentPerson, currentEditors);
        currentPerson.updatedAt = new Date();
        peopleList.repaint();
    }

    private JComponent editorForField(FieldDefinition f, Object current) {
        switch (f.type) {
            case TEXT: {
                JTextField tf = new JTextField(current==null?"":String.valueOf(current));
                return tf;
            }
            case LONG_TEXT: {
                JTextArea ta = new JTextArea(current==null?"":String.valueOf(current), 5, 20);
                ta.setLineWrap(true); ta.setWrapStyleWord(true);
                return new JScrollPane(ta);
            }
            case NUMBER: {
                JTextField tf = new JTextField(current==null?"":String.valueOf(current));
                tf.setToolTipText(tr("Enter a number"));
                return tf;
            }
            case BOOLEAN: {
                JCheckBox cb = new JCheckBox();
                cb.setSelected(Boolean.TRUE.equals(current));
                return cb;
            }
            case DATE: {
                JPanel p = new JPanel(new BorderLayout());
                JTextField tf = new JTextField(current==null?"":String.valueOf(current));
                tf.setToolTipText("yyyy-MM-dd");
                JButton today = new JButton(tr("Today"));
                today.addActionListener(e -> tf.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                p.add(tf, BorderLayout.CENTER); p.add(today, BorderLayout.EAST);
                return p;
            }
            case LIST: {
                return buildListEditor(current);
            }
            case ENUM: {
                JComboBox<String> cb = new JComboBox<>();
                if (f.enumOptions != null) {
                    for (String opt : f.enumOptions) cb.addItem(opt);
                }
                if (current != null) cb.setSelectedItem(String.valueOf(current));
                return cb;
            }
            case IMAGE_PATH: {
                return buildImagePicker(current);
            }
        }
        return new JLabel(tr("Unsupported"));
    }

    private JComponent buildListEditor(Object current) {
        DefaultListModel<String> model = new DefaultListModel<>();
        if (current instanceof List) {
            @SuppressWarnings("unchecked") List<Object> lst = (List<Object>) current;
            for (Object o : lst) model.addElement(String.valueOf(o));
        }
        JList<String> list = new JList<>(model);
        list.setVisibleRowCount(4);
        JScrollPane sp = new JScrollPane(list);
        JTextField input = new JTextField();
        JButton add = new JButton(tr("Add"));
        add.addActionListener(e -> { String t = input.getText(); if (!t.isBlank()) { model.addElement(t); input.setText(""); }});
        JButton remove = new JButton(tr("Delete"));
        remove.addActionListener(e -> { int idx = list.getSelectedIndex(); if (idx>=0) model.remove(idx); });
        JPanel controls = new JPanel(new BorderLayout());
        controls.add(input, BorderLayout.CENTER);
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(add); btns.add(remove);
        controls.add(btns, BorderLayout.EAST);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(sp, BorderLayout.CENTER);
        panel.add(controls, BorderLayout.SOUTH);
        panel.putClientProperty("list-model", model);
        return panel;
    }

    private JComponent buildImagePicker(Object current) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel preview = new JLabel(tr("No preview"), SwingConstants.CENTER);
        preview.setPreferredSize(new Dimension(140, 140));
        preview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        preview.setOpaque(true);
        preview.setBackground(Color.WHITE);

        JTextField nameField = new JTextField();
        nameField.setEditable(false);

        JButton choose = new JButton(tr("Browse..."));
        choose.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileFilter(new FileNameExtensionFilter(tr("Images"), "png","jpg","jpeg","gif","bmp"));
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                EmbeddedResource resource = createResourceFromFile(f);
                if (resource != null) {
                    panel.putClientProperty("embedded-resource", resource);
                    panel.putClientProperty("legacy-path", null);
                    nameField.setText(resource.fileName);
                    updateImagePreview(preview, resource);
                }
            }
        });

        JButton clear = new JButton(tr("Clear"));
        clear.addActionListener(e -> {
            panel.putClientProperty("embedded-resource", null);
            panel.putClientProperty("legacy-path", null);
            nameField.setText("");
            preview.setIcon(null);
            preview.setText(tr("No preview"));
        });

        JPanel controlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controlButtons.add(choose);
        controlButtons.add(clear);

        JPanel top = new JPanel(new BorderLayout());
        top.add(nameField, BorderLayout.CENTER);
        top.add(controlButtons, BorderLayout.EAST);

        panel.add(top, BorderLayout.NORTH);
        panel.add(preview, BorderLayout.CENTER);

        EmbeddedResource existing = toEmbeddedResource(current);
        if (existing != null) {
            panel.putClientProperty("embedded-resource", existing);
            nameField.setText(existing.fileName);
            updateImagePreview(preview, existing);
        } else if (current instanceof String) {
            panel.putClientProperty("legacy-path", current);
            nameField.setText(String.valueOf(current));
        }

        panel.putClientProperty("image-name-field", nameField);
        panel.putClientProperty("image-preview", preview);
        return panel;
    }

    private void updateImagePreview(JLabel lbl, EmbeddedResource resource) {
        if (resource == null || resource.base64Data == null || resource.base64Data.isBlank()) {
            lbl.setIcon(null);
            lbl.setText(tr("No preview"));
            return;
        }
        try {
            byte[] data = resource.decode();
            ImageIcon icon = new ImageIcon(data);
            Image scaled = scaleToLabel(icon.getImage(), lbl);
            lbl.setText(" ");
            lbl.setIcon(new ImageIcon(scaled));
        } catch (Exception ex) {
            lbl.setIcon(null);
            lbl.setText(tr("Image error"));
        }
    }

    private Image scaleToLabel(Image img, JLabel lbl) {
        int w = Math.max(1, lbl.getWidth());
        int h = Math.max(1, lbl.getHeight());
        if (w == 1 && h == 1) {
            w = 140;
            h = 140;
        }
        int imgW = img.getWidth(null);
        int imgH = img.getHeight(null);
        if (imgW <= 0 || imgH <= 0) {
            return img.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        }
        double ratio = Math.min((double) w / imgW, (double) h / imgH);
        int newW = Math.max(1, (int) Math.round(imgW * ratio));
        int newH = Math.max(1, (int) Math.round(imgH * ratio));
        return img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
    }

    private EmbeddedResource createResourceFromFile(File file) {
        return createResourceFromFile(file, true);
    }

    private EmbeddedResource createResourceFromFile(File file, boolean showError) {
        if (file == null || !file.exists()) {
            if (showError) {
                JOptionPane.showMessageDialog(this, tr("File not found."), tr("Error"), JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(data);
            String mime = Files.probeContentType(file.toPath());
            return new EmbeddedResource(file.getName(), mime, base64);
        } catch (IOException ex) {
            if (showError) {
                JOptionPane.showMessageDialog(this, trf("Failed to load file: %s", ex.getMessage()), tr("Error"), JOptionPane.ERROR_MESSAGE);
            }
            return null;
        }
    }

    private EmbeddedResource toEmbeddedResource(Object current) {
        if (current instanceof EmbeddedResource) {
            return (EmbeddedResource) current;
        }
        if (current instanceof String) {
            String path = String.valueOf(current);
            if (!path.isBlank()) {
                return createResourceFromFile(new File(path), false);
            }
        }
        return null;
    }

    private Object readEditorValue(FieldDefinition f, JComponent ed) {
        switch (f.type) {
            case TEXT: {
                if (ed instanceof JTextField) return ((JTextField) ed).getText();
                if (ed instanceof JScrollPane) return ((JTextArea)((JScrollPane) ed).getViewport().getView()).getText();
                return null;
            }
            case LONG_TEXT: {
                if (ed instanceof JScrollPane) return ((JTextArea)((JScrollPane) ed).getViewport().getView()).getText();
                return null;
            }
            case NUMBER: {
                if (ed instanceof JTextField) {
                    String t = ((JTextField) ed).getText().trim();
                    if (t.isBlank()) return null; // keep null
                    try {
                        if (t.contains(".")) return Double.parseDouble(t);
                        else return Long.parseLong(t);
                    } catch (NumberFormatException nfe) {
                        return t; // store as string if unparsable
                    }
                }
                return null;
            }
            case BOOLEAN: {
                if (ed instanceof JCheckBox) return ((JCheckBox) ed).isSelected();
                return Boolean.FALSE;
            }
            case DATE: {
                if (ed instanceof JPanel) {
                    JTextField tf = (JTextField)((JPanel) ed).getComponent(0);
                    String t = tf.getText().trim();
                    if (t.isBlank()) return null;
                    // store as string yyyy-MM-dd
                    try { dateFormat.parse(t); } catch (ParseException ignored) {}
                    return t;
                }
                return null;
            }
            case LIST: {
                if (ed instanceof JPanel) {
                    @SuppressWarnings("unchecked") DefaultListModel<String> model = (DefaultListModel<String>) ((JPanel) ed).getClientProperty("list-model");
                    List<String> items = new ArrayList<>();
                    if (model != null) for (int i=0;i<model.size();i++) items.add(model.get(i));
                    return items;
                }
                return new ArrayList<String>();
            }
            case ENUM: {
                if (ed instanceof JComboBox) {
                    Object v = ((JComboBox<?>) ed).getSelectedItem();
                    return v==null?null:String.valueOf(v);
                }
                return null;
            }
            case IMAGE_PATH: {
                if (ed instanceof JPanel) {
                    EmbeddedResource res = (EmbeddedResource) ((JPanel) ed).getClientProperty("embedded-resource");
                    if (res != null) {
                        return res;
                    }
                    Object legacy = ((JPanel) ed).getClientProperty("legacy-path");
                    if (legacy instanceof String) {
                        return legacy;
                    }
                    return null;
                }
                return null;
            }
        }
        return null;
    }

    private void addFieldDialog() {
        JTextField name = new JTextField();
        JComboBox<FieldType> type = new JComboBox<>(FieldType.values());
        JTextField enumOpts = new JTextField(); enumOpts.setToolTipText(tr("Comma separated values for ENUM"));
        JPanel p = new JPanel(new GridLayout(0,1,6,6));
        p.add(new JLabel(tr("Field name"))); p.add(name);
        p.add(new JLabel(tr("Type"))); p.add(type);
        p.add(new JLabel(tr("ENUM options (comma separated)"))); p.add(enumOpts);
        int ok = JOptionPane.showConfirmDialog(this, p, tr("Add Field"), JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            String nm = name.getText().trim();
            if (nm.isBlank()) { JOptionPane.showMessageDialog(this, tr("Please enter a field name")); return; }
            FieldDefinition fd = new FieldDefinition(nm, (FieldType) type.getSelectedItem());
            if (fd.type == FieldType.ENUM) {
                if (!enumOpts.getText().isBlank()) fd.enumOptions = Arrays.stream(enumOpts.getText().split(",")).map(String::trim).filter(s->!s.isBlank()).collect(Collectors.toList());
                else fd.enumOptions = new ArrayList<>();
            }
            try {
                db.addFieldToSchema(fd);
                schemaTableModel.fireTableDataChanged();
                refreshSearchFieldCombo();
                buildDetailsForm(peopleList.getSelectedValue());
                persistDefaultSchema();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), tr("Error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void removeFieldDialog() {
        List<String> names = db.schema.fieldNames();
        String sel = (String) JOptionPane.showInputDialog(this, tr("Select a field to remove"), tr("Remove Field"), JOptionPane.PLAIN_MESSAGE, null, names.toArray(), null);
        if (sel == null) return;
        int c = JOptionPane.showConfirmDialog(this, trf("Delete field '%s'? This will remove it from all data.", sel), tr("Confirm"), JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            db.schema.removeField(sel);
            for (PersonRecord p : db.people) p.data.remove(sel);
            schemaTableModel.fireTableDataChanged();
            refreshSearchFieldCombo();
            buildDetailsForm(peopleList.getSelectedValue());
            persistDefaultSchema();
        }
    }

    // --- Schema table model ---
    class SchemaTableModel extends AbstractTableModel {
        private final String[] cols = {"Name", "Type", "Enum Options"};

        @Override public int getRowCount() { return db.schema.fields.size(); }
        @Override public int getColumnCount() { return cols.length; }
        @Override public String getColumnName(int c){ return tr(cols[c]); }
        @Override public Object getValueAt(int r, int c) {
            FieldDefinition f = db.schema.fields.get(r);
            switch (c) {
                case 0: return f.name;
                case 1: return f.type;
                case 2: return f.enumOptions==null?"":String.join(", ", f.enumOptions);
            }
            return null;
        }
        @Override public boolean isCellEditable(int r, int c) { return c==0 || c==2; }
        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            FieldDefinition f = db.schema.fields.get(rowIndex);
            if (columnIndex==0) {
                String newName = String.valueOf(aValue).trim();
                if (newName.isBlank() || newName.equals(f.name)) return;
                FieldDefinition existing = db.schema.getField(newName);
                if (existing != null && existing != f) {
                    JOptionPane.showMessageDialog(V1_4_0___PersonalDBApp.this, tr("Field name already exists."));
                    return;
                }
                String oldName = f.name;
                f.name = newName;
                for (PersonRecord p : db.people) {
                    Object val = p.data.remove(oldName);
                    p.data.put(newName, val);
                }
                peopleList.repaint();
            }
            if (columnIndex==2) {
                List<String> opts = Arrays.stream(String.valueOf(aValue).split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .collect(Collectors.toList());
                f.enumOptions = opts;
            }
            fireTableRowsUpdated(rowIndex, rowIndex);
            refreshSearchFieldCombo();
            buildDetailsForm(peopleList.getSelectedValue());
            persistDefaultSchema();
        }
    }

    // --- Project save/open ---
    private void newProject() {
        int c = JOptionPane.showConfirmDialog(this, tr("Would you like to save the current project?"), tr("New"), JOptionPane.YES_NO_CANCEL_OPTION);
        if (c == JOptionPane.CANCEL_OPTION) return;
        if (c == JOptionPane.YES_OPTION) saveProject(false);
        db.people.clear();
        db.schema = new Schema();
        ensureConfigDirectoryExists();
        if (!loadGlobalSchemaFromConfig()) {
            seedInitialSchema(db.schema);
        }
        persistDefaultSchema();
        currentProjectFile = null;
        File active = getActiveProjectFile();
        if (active != null) {
            writeProjectToFile(active, false);
        }
        refreshPeopleList();
        buildDetailsForm(null);
    }

    private void openProject() {
        JFileChooser fc = createFileChooser(tr("Personal DB (*.pdb)"), "pdb");
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            saveCurrentPerson();
            File previousLock = lockedProjectFile;
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                Object loadedObj = ois.readObject();
                if (loadedObj instanceof AppState) {
                    AppState state = (AppState) loadedObj;
                    applyLoadedState(state);
                } else if (loadedObj instanceof PersonalDatabase) {
                    PersonalDatabase loaded = (PersonalDatabase) loadedObj;
                    AppState state = new AppState();
                    state.database = loaded;
                    state.settings = settings;
                    applyLoadedState(state);
                } else {
                    throw new IOException(tr("Unknown project format."));
                }
                lockProjectFile(f);
                currentProjectFile = f;
                JOptionPane.showMessageDialog(this, trf("Loaded %s", f.getName()));
            } catch (Exception ex) {
                if (previousLock != null && !previousLock.equals(f)) {
                    try {
                        lockProjectFile(previousLock);
                    } catch (IOException ignored) {}
                }
                JOptionPane.showMessageDialog(this, trf("Failed to open: %s", ex.getMessage()), tr("Error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveProject(boolean saveAs) {
        saveCurrentPerson();
        File target = currentProjectFile;
        if (saveAs || target == null) {
            JFileChooser fc = createFileChooser(tr("Personal DB (*.pdb)"), "pdb");
            if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
            target = ensureExt(fc.getSelectedFile(), ".pdb");
        }
        if (target == null) {
            target = ensureDefaultProjectFile();
        }
        if (target == null) return;
        if (writeProjectToFile(target, true)) {
            currentProjectFile = target;
            showSystemNotification(tr("PersonalDB"), trf("Saved to %s", target.getName()));
            JOptionPane.showMessageDialog(this, trf("Saved to %s", target.getName()));
        }
    }

    private File getActiveProjectFile() {
        if (currentProjectFile != null) {
            return currentProjectFile;
        }
        return ensureDefaultProjectFile();
    }

    private File ensureDefaultProjectFile() {
        if (settings == null) return null;
        File base = settings.getDataDirectoryFile();
        if (base != null && !base.exists()) {
            base.mkdirs();
        }
        if (defaultProjectFile == null || (base != null && !Objects.equals(defaultProjectFile.getParentFile(), base))) {
            defaultProjectFile = new File(base, DEFAULT_PROJECT_FILE_NAME);
        }
        if (defaultProjectFile == null) {
            return null;
        }
        boolean needInitialContent = !defaultProjectFile.exists();
        try {
            lockProjectFile(defaultProjectFile);
            if (!needInitialContent && projectLockRaf != null) {
                needInitialContent = projectLockRaf.length() == 0;
            }
            if (needInitialContent) {
                writeProjectToFile(defaultProjectFile, false);
            }
        } catch (Exception ex) {
            System.err.println("Failed to prepare default project file: " + ex.getMessage());
            return null;
        }
        return defaultProjectFile;
    }

    private synchronized void lockProjectFile(File file) throws IOException {
        if (file == null) return;
        if (lockedProjectFile != null && lockedProjectFile.equals(file) && projectFileLock != null && projectFileLock.isValid()) {
            return;
        }
        releaseProjectLock();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        projectLockRaf = new RandomAccessFile(file, "rw");
        FileChannel channel = projectLockRaf.getChannel();
        projectFileLock = channel.lock();
        lockedProjectFile = file;
    }

    private synchronized void releaseProjectLock() {
        try {
            if (projectFileLock != null && projectFileLock.isValid()) {
                projectFileLock.release();
            }
        } catch (IOException ignored) {}
        projectFileLock = null;
        if (projectLockRaf != null) {
            try { projectLockRaf.close(); } catch (IOException ignored) {}
        }
        projectLockRaf = null;
        lockedProjectFile = null;
    }

    private synchronized void lockSchemaFile(File file) throws IOException {
        if (file == null) return;
        if (lockedSchemaFile != null && lockedSchemaFile.equals(file) && schemaFileLock != null && schemaFileLock.isValid()) {
            return;
        }
        releaseSchemaLock();
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        schemaLockRaf = new RandomAccessFile(file, "rw");
        FileChannel channel = schemaLockRaf.getChannel();
        schemaFileLock = channel.lock();
        lockedSchemaFile = file;
    }

    private synchronized void releaseSchemaLock() {
        try {
            if (schemaFileLock != null && schemaFileLock.isValid()) {
                schemaFileLock.release();
            }
        } catch (IOException ignored) {}
        schemaFileLock = null;
        if (schemaLockRaf != null) {
            try { schemaLockRaf.close(); } catch (IOException ignored) {}
        }
        schemaLockRaf = null;
        lockedSchemaFile = null;
    }

    private void releaseLocks() {
        releaseProjectLock();
        releaseSchemaLock();
    }

    private boolean writeProjectToFile(File target, boolean showErrorDialog) {
        if (target == null) return false;
        try {
            lockProjectFile(target);
            if (projectLockRaf == null) {
                throw new IOException("Project file handle unavailable");
            }
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
                oos.writeObject(new AppState(db, settings));
            }
            byte[] data = buffer.toByteArray();
            projectLockRaf.setLength(0);
            projectLockRaf.seek(0);
            projectLockRaf.write(data);
            projectLockRaf.getChannel().force(true);
            return true;
        } catch (Exception ex) {
            if (showErrorDialog) {
                JOptionPane.showMessageDialog(this, trf("Failed to save: %s", ex.getMessage()), tr("Error"), JOptionPane.ERROR_MESSAGE);
            } else {
                System.err.println("Failed to save project: " + ex.getMessage());
            }
            return false;
        }
    }

    private void startAutoSaveTimer() {
        stopAutoSaveTimer();
        autoSaveTimer = new javax.swing.Timer(5 * 60 * 1000, e -> performAutoSave());
        autoSaveTimer.setRepeats(true);
        autoSaveTimer.start();
    }

    private void stopAutoSaveTimer() {
        if (autoSaveTimer != null) {
            autoSaveTimer.stop();
            autoSaveTimer = null;
        }
    }

    private void performAutoSave() {
        saveCurrentPerson();
        File target = getActiveProjectFile();
        if (target == null) return;
        if (writeProjectToFile(target, false)) {
            if (currentProjectFile == null) {
                currentProjectFile = target;
            }
            showSystemNotification(tr("PersonalDB"), trf("Auto-saved to %s", target.getName()));
        }
    }

    private void performQuickSave() {
        saveCurrentPerson();
        File target = getActiveProjectFile();
        if (target == null) return;
        if (writeProjectToFile(target, true)) {
            if (currentProjectFile == null) {
                currentProjectFile = target;
            }
            showSystemNotification(tr("PersonalDB"), trf("Saved to %s", target.getName()));
        }
    }

    private void initializeTrayIcon() {
        if (!SystemTray.isSupported()) {
            return;
        }
        if (trayIcon != null) {
            return;
        }
        SystemTray tray = SystemTray.getSystemTray();
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setColor(new Color(0x2F, 0x7D, 0xD3));
        g2.fillRect(0, 0, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(g2.getFont().deriveFont(Font.BOLD, 12f));
        g2.drawString("P", 3, 12);
        g2.dispose();
        trayIcon = new TrayIcon(image, tr("PersonalDB"));
        trayIcon.setImageAutoSize(true);
        try {
            tray.add(trayIcon);
        } catch (AWTException ex) {
            trayIcon = null;
        }
    }

    private void removeTrayIcon() {
        if (trayIcon != null && SystemTray.isSupported()) {
            SystemTray.getSystemTray().remove(trayIcon);
            trayIcon = null;
        }
    }

    private void showSystemNotification(String title, String message) {
        if (message == null || message.isBlank()) {
            return;
        }
        if (trayIcon == null) {
            initializeTrayIcon();
        }
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        } else {
            System.out.println(title + ": " + message);
        }
    }

    private void requestExit() {
        saveCurrentPerson();
        int option = JOptionPane.showConfirmDialog(this, tr("Save project before exiting?"), tr("Exit"), JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.CANCEL_OPTION || option == JOptionPane.CLOSED_OPTION) {
            return;
        }
        if (option == JOptionPane.YES_OPTION) {
            File target = getActiveProjectFile();
            if (target != null) {
                if (!writeProjectToFile(target, true)) {
                    return;
                }
            }
        }
        shutdown();
    }

    private void shutdown() {
        stopAutoSaveTimer();
        stopLanguagePackWatcher();
        removeTrayIcon();
        releaseLocks();
        dispose();
    }

    private void initializeDefaultFiles() {
        ensureDefaultProjectFile();
        if (defaultSchemaFile == null) {
            defaultSchemaFile = new File(DEFAULT_CONFIG_DIRECTORY, DEFAULT_SCHEMA_FILE_NAME);
        }
        boolean needSchema = defaultSchemaFile != null && !defaultSchemaFile.exists();
        try {
            lockSchemaFile(defaultSchemaFile);
            if (needSchema || (schemaLockRaf != null && schemaLockRaf.length() == 0)) {
                persistDefaultSchema();
            }
        } catch (Exception ex) {
            System.err.println("Failed to prepare schema file: " + ex.getMessage());
        }
    }

    private void persistDefaultSchema() {
        if (defaultSchemaFile == null) {
            defaultSchemaFile = new File(DEFAULT_CONFIG_DIRECTORY, DEFAULT_SCHEMA_FILE_NAME);
        }
        try {
            lockSchemaFile(defaultSchemaFile);
            if (schemaLockRaf == null) {
                throw new IOException("Schema file handle unavailable");
            }
            Schema snapshot = db.snapshotSchema();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            try (ObjectOutputStream oos = new ObjectOutputStream(buffer)) {
                oos.writeObject(snapshot);
            }
            byte[] data = buffer.toByteArray();
            schemaLockRaf.setLength(0);
            schemaLockRaf.seek(0);
            schemaLockRaf.write(data);
            schemaLockRaf.getChannel().force(true);
        } catch (Exception ex) {
            System.err.println("Failed to persist schema: " + ex.getMessage());
        }
    }

    private void exportAllJson() {
        JFileChooser fc = createFileChooser(tr("JSON (*.json)"), "json");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ensureExt(fc.getSelectedFile(), ".json");
            try { db.exportAllToJson(f); JOptionPane.showMessageDialog(this, trf("Exported: %s", f.getAbsolutePath())); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), tr("Error"), JOptionPane.ERROR_MESSAGE); }
        }
    }
    private void exportOneJson() {
        PersonRecord sel = peopleList.getSelectedValue();
        if (sel == null) { JOptionPane.showMessageDialog(this, tr("No person selected.")); return; }
        JFileChooser fc = createFileChooser(tr("JSON (*.json)"), "json");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ensureExt(fc.getSelectedFile(), ".json");
            try { db.exportOneToJson(sel, f); JOptionPane.showMessageDialog(this, trf("Exported: %s", f.getAbsolutePath())); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), tr("Error"), JOptionPane.ERROR_MESSAGE); }
        }
    }
    private void exportAllCsv() {
        JFileChooser fc = createFileChooser(tr("CSV (*.csv)"), "csv");
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = ensureExt(fc.getSelectedFile(), ".csv");
            try { db.exportAllToCsv(f); JOptionPane.showMessageDialog(this, trf("Exported: %s", f.getAbsolutePath())); }
            catch (Exception ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), tr("Error"), JOptionPane.ERROR_MESSAGE); }
        }
    }

    private static File ensureExt(File f, String ext) {
        String n = f.getName();
        if (!n.toLowerCase().endsWith(ext)) {
            File parent = f.getParentFile();
            if (parent != null) {
                return new File(parent, n + ext);
            }
            return new File(n + ext);
        }
        return f;
    }

    private JFileChooser createFileChooser(String description, String extension) {
        File base = settings.getDataDirectoryFile();
        JFileChooser fc = base.exists() ? new JFileChooser(base) : new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(description, extension));
        return fc;
    }

    private void applyLoadedState(AppState state) {
        if (state == null || state.database == null) return;
        PersonalDatabase loaded = state.database;
        db.schema = loaded.schema != null ? loaded.schema : new Schema();
        db.people = loaded.people != null ? loaded.people : new ArrayList<>();
        if (state.settings != null) {
            settings.applyFrom(state.settings);
        }
        settings.ensureDataDirectoryExists();
        defaultProjectFile = new File(settings.getDataDirectoryFile(), DEFAULT_PROJECT_FILE_NAME);
        syncLanguageFromSettings();
        refreshPeopleList();
        schemaTableModel.fireTableDataChanged();
        buildDetailsForm(null);
        refreshSearchFieldCombo();
        refreshCompareCombos();
        peopleList.repaint();
        persistDefaultSchema();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
            new V1_4_0___PersonalDBApp().setVisible(true);
        });
    }
}