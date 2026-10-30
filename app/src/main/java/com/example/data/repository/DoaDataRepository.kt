package com.example.data.repository

import com.example.data.local.DoaBookmarkDao
import com.example.data.local.DoaBookmarkEntity
import com.example.data.model.DoaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class DoaDataRepository(private val bookmarkDao: DoaBookmarkDao) {

    private val customAndAiDoas = MutableStateFlow<List<DoaItem>>(emptyList())

    val allCategories = listOf(
        "Semua",
        "Al-Qur'an",
        "Hadist Shahih",
        "Penenang Hati",
        "Harian",
        "Rezeki",
        "Kesembuhan",
        "Perlindungan"
    )

    private val initialDoas = listOf(
        DoaItem(
            id = "quran_01",
            title = "Doa Sapu Jagat (Kebaikan Dunia & Akhirat)",
            category = "Al-Qur'an",
            arabicText = "رَبَّنَا آتِنَا فِي الدُّنْيَا حَسَنَةً وَفِي الْآخِرَةِ حَسَنَةً وَقِنَا عَذَابَ النَّارِ",
            latinText = "Rabbana atina fid-dunya hasanah, wa fil-akhirati hasanah, wa qina 'adzaban-nar",
            translation = "Ya Tuhan kami, berilah kami kebaikan di dunia dan kebaikan di akhirat, dan lindungilah kami dari azab neraka.",
            source = "QS. Al-Baqarah: 201",
            benefit = "Doa yang paling sering dibaca oleh Rasulullah SAW karena mencakup seluruh permohonan kebaikan materi, ruhani, dan keselamatan abadi.",
            recommendedCount = 3
        ),
        DoaItem(
            id = "quran_02",
            title = "Doa Nabi Yunus (Penghilang Duka & Kesulitan Hidup)",
            category = "Penenang Hati",
            arabicText = "لَا إِلَهَ إِلَّا أَنْتَ سُبْحَانَكَ إِنِّي كُنْتُ مِنَ الظَّالِمِينَ",
            latinText = "La ilaha illa anta subhanaka inni kuntu minaz-zhalimin",
            translation = "Tidak ada Tuhan selain Engkau. Maha Suci Engkau, sungguh aku adalah termasuk orang-orang yang zalim.",
            source = "QS. Al-Anbiya: 87 (HR. Tirmidzi)",
            benefit = "Rasulullah SAW bersabda: 'Doa Dzun Nun (Nabi Yunus) saat di dalam perut ikan paus; tidaklah seorang muslim berdoa dengannya dalam suatu masalah melainkan Allah akan mengabulkan doanya.'",
            recommendedCount = 3
        ),
        DoaItem(
            id = "hadist_01",
            title = "Doa Perlindungan dari Kegelisahan, Sedih & Hutang",
            category = "Hadist Shahih",
            arabicText = "اللَّهُمَّ إِنِّي أَعُوذُ بِكَ مِنَ الْهَمِّ وَالْحَزَنِ، وَأَعُوذُ بِكَ مِنَ الْعَجْزِ وَالْكَسَلِ، وَأَعُوذُ بِكَ مِنَ الْجُبْنِ وَالْبُخْلِ، وَأَعُوذُ بِكَ مِنْ غَلَبَةِ الدَّيْنِ وَقَهْرِ الرِّجَالِ",
            latinText = "Allahumma inni a'udzubika minal-hammi wal-hazan, wa a'udzubika minal-'ajzi wal-kasal, wa a'udzubika minal-jubni wal-bukhl, wa a'udzubika min ghalabatid-daini wa qahrir-rijal",
            translation = "Ya Allah, sesungguhnya aku berlindung kepada-Mu dari rasa gelisah dan sedih, dari kelemahan dan kemalasan, dari sifat pengecut dan kikir, serta dari lilitan hutang dan kesewenang-wenangan orang lain.",
            source = "HR. Bukhari no. 6369 & Abu Dawud no. 1555",
            benefit = "Diajarkan Nabi SAW kepada sahabat Abu Umamah saat dirundung kesedihan dan hutang menumpuk di masjid.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "hadist_02",
            title = "Sayyidul Istighfar (Puncak Doa Permohonan Ampun)",
            category = "Hadist Shahih",
            arabicText = "اللَّهُمَّ أَنْتَ رَبِّي لَا إِلَهَ إِلَّا أَنْتَ، خَلَقْتَنِي وَأَنَا عَبْدُكَ، وَأَنَا عَلَى عَهْدِكَ وَوَعْدِكَ مَا اسْتَطَعْتُ، أَعُوذُ بِكَ مِنْ شَرِّ مَا صَنَعْتُ، أَبُوءُ لَكَ بِنِعْمَتِكَ عَلَيَّ، وَأَبُوءُ لَكَ بِذَنْبِي فَاغْفِرْ لِي فَإِنَّهُ لَا يَغْفِرُ الذُّنُوبَ إِلَّا أَنْتَ",
            latinText = "Allahumma anta Rabbi la ilaha illa anta, khalaqtani wa ana 'abduka, wa ana 'ala 'ahdika wa wa'dika mastatha'tu, a'udzubika min syarri ma shana'tu, abu'u laka bini'matika 'alayya, wa abu'u laka bidzanbi faghfir li, fa innahu la yaghfirudz-dzunuba illa anta",
            translation = "Ya Allah, Engkau adalah Rabbku, tiada Tuhan selain Engkau. Engkau yang menciptakanku dan aku adalah hamba-Mu. Aku senantiasa menepati janji-Mu semampuku. Aku berlindung dari keburukan perbuatanku, mengakui limpahan nikmat-Mu dan mengakui dosaku. Maka ampunilah aku, karena tak ada yang dapat mengampuni dosa selain Engkau.",
            source = "HR. Bukhari no. 6306",
            benefit = "Barangsiapa membacanya di petang hari lalu meninggal malam itu atau membacanya di pagi hari lalu meninggal siang itu, maka ia termasuk penghuni surga.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "quran_03",
            title = "Doa Kelapangan Hati & Kemudahan Urusan (Nabi Musa)",
            category = "Al-Qur'an",
            arabicText = "رَبِّ اشْرَحْ لِي صَدْرِي وَيَسِّرْ لِي أَمْرِي وَاحْلُلْ عُقْدَةً مِنْ لِسَانِي يَفْقَهُوا قَوْلِي",
            latinText = "Rabbisy-syrah li shadri, wa yassir li amri, wahlul 'uqdatan min lisani, yafqahu qauli",
            translation = "Ya Tuhanku, lapangkanlah dadaku, mudahkanlah untukku urusanku, dan lepaskanlah kekakuan dari lidahku, agar mereka mengerti perkataanku.",
            source = "QS. Thaha: 25-28",
            benefit = "Doa yang sangat mustajab sebelum berbicara di depan umum, menghadapi ujian, rapat penting, wawancara, atau saat batin terasa sesak.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "hadist_03",
            title = "Doa Kesembuhan dari Segala Penyakit",
            category = "Kesembuhan",
            arabicText = "اللَّهُمَّ رَبَّ النَّاسِ أَذْهِبِ الْبَأْسَ، اشْفِ أَنْتَ الشَّافِي، لَا شِفَاءَ إِلَّا شِفَاؤُكَ، شِفَاءً لَا يُغَادِرُ سَقَمًا",
            latinText = "Allahumma Rabban-nasi adzhibil-ba'sa, isyfi antasy-syafi, la syifa'a illa syifa'uka, syifa'an la yughadiru saqama",
            translation = "Ya Allah, Tuhan seluruh manusia, hilangkanlah rasa sakit ini, sembuhkanlah! Engkaulah Dzat Maha Penyembuh, tiada kesembuhan selain kesembuhan dari-Mu, kesembuhan yang tidak meninggalkan rasa sakit sedikit pun.",
            source = "HR. Bukhari no. 5742 & Muslim no. 2191",
            benefit = "Dibaca Nabi SAW sambil mengusapkan tangan kanan ke tubuh orang yang sakit.",
            recommendedCount = 3
        ),
        DoaItem(
            id = "hadist_04",
            title = "Doa Memohon Keteguhan Hati dalam Kebaikan",
            category = "Penenang Hati",
            arabicText = "يَا مُقَلِّبَ الْقُلُوبِ ثَبِّتْ قَلْبِي عَلَى دِينِكَ وَطَاعَتِكَ",
            latinText = "Ya Muqallibal-qulub, tsabbit qalbi 'ala dinika wa tha'atika",
            translation = "Wahai Dzat yang membolak-balikkan hati, teguhkanlah hatiku di atas agama-Mu dan ketaatan kepada-Mu.",
            source = "HR. Tirmidzi no. 2140 & Ahmad",
            benefit = "Ummul Mukminin Ummu Salamah meriwayatkan bahwa ini adalah doa yang paling sering diucapkan oleh Rasulullah SAW.",
            recommendedCount = 3
        ),
        DoaItem(
            id = "quran_04",
            title = "Doa Memohon Keturunan & Pasangan Penyejuk Hati",
            category = "Al-Qur'an",
            arabicText = "رَبَّنَا هَبْ لَنَا مِنْ أَزْوَاجِنَا وَذُرِّيَّاتِنَا قُرَّةَ أَعْيُنٍ وَاجْعَلْنَا لِلْمُتَّقِينَ إِمَامًا",
            latinText = "Rabbana hab lana min azwajina wa dzurriyyatina qurrata a'yun, waj'alna lil-muttaqina imama",
            translation = "Ya Tuhan kami, anugerahkanlah kepada kami pasangan kami dan keturunan kami sebagai penyenang hati (kami), dan jadikanlah kami pemimpin bagi orang-orang yang bertakwa.",
            source = "QS. Al-Furqan: 74",
            benefit = "Doa membina rumah tangga sakinah, mawaddah, rahmah dan anak-anak yang shalih serta berbakti.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "hadist_05",
            title = "Doa Memohon Kelapangan Rezeki Halal & Ilmu Bermanfaat",
            category = "Rezeki",
            arabicText = "اللَّهُمَّ إِنِّي أَسْأَلُكَ عِلْمًا نَافِعًا، وَرِزْقًا طَيِّبًا، وَعَمَلًا مُتَقَبَّلًا",
            latinText = "Allahumma inni as'aluka 'ilman nafi'an, wa rizqan thayyiban, wa 'amalan mutaqabbala",
            translation = "Ya Allah, sesungguhnya aku memohon kepada-Mu ilmu yang bermanfaat, rezeki yang halal dan baik, serta amal perbuatan yang diterima.",
            source = "HR. Ibnu Majah no. 925 & Ahmad",
            benefit = "Dibaca Rasulullah SAW setiap selesai shalat Subuh setelah salam untuk membuka pintu rezeki dan keberkahan harian.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "quran_05",
            title = "Doa Syukur atas Segala Limpahan Nikmat (Nabi Sulaiman)",
            category = "Al-Qur'an",
            arabicText = "رَبِّ أَوْزِعْنِي أَنْ أَشْكُرَ نِعْمَتَكَ الَّتِي أَنْعَمْتَ عَلَيَّ وَعَلَى وَالِدَيَّ وَأَنْ أَعْمَلَ صَالِحًا تَرْضَاهُ وَأَدْخِلْنِي بِرَحْمَتِكَ فِي عِبَادِكَ الصَّالِحِينَ",
            latinText = "Rabbi awzi'ni an asykura ni'matakal-lati an'amta 'alayya wa 'ala walidayya wa an a'mala shalihan tardhahu wa adkhilni birahmatika fi 'ibadikas-shalihin",
            translation = "Ya Tuhanku, anugerahkanlah aku ilham untuk tetap mensyukuri nikmat-Mu yang telah Engkau limpahkan kepadaku dan kepada kedua orang tuaku, dan untuk mengerjakan kebajikan yang Engkau ridhai; dan masukkanlah aku dengan rahmat-Mu ke dalam golongan hamba-hamba-Mu yang saleh.",
            source = "QS. An-Naml: 19",
            benefit = "Membangkitkan rasa syukur mendalam, keharmonisan berbakti kepada orang tua, dan perlindungan dari sifat takabur.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "hadist_06",
            title = "Doa Melegakan Amarah & Emosi yang Meluap",
            category = "Penenang Hati",
            arabicText = "أَعُوذُ بِاللَّهِ مِنَ الشَّيْطَانِ الرَّجِيمِ، اللَّهُمَّ اغْفِرْ لِي ذَنْبِي وَأَذْهِبْ غَيْظَ قَلْبِي",
            latinText = "A'udzu billahi minasy-syaithanir-rajim, Allahummaghfir li dzanbi wa adzhib ghaizha qalbi",
            translation = "Aku berlindung kepada Allah dari godaan setan yang terkutuk. Ya Allah, ampunilah dosaku dan hilangkanlah kobaran amarah dari hatiku.",
            source = "HR. Bukhari no. 6115 & Ibnu Sunni",
            benefit = "Nabi SAW bersabda mengenai orang yang marah: 'Sungguh aku mengetahui suatu kalimat, bila diucapkannya niscaya hilang apa yang dirasakannya, yaitu ta'awudz.'",
            recommendedCount = 1
        ),
        DoaItem(
            id = "hadist_07",
            title = "Doa Istikharah (Memohon Petunjuk di Kala Bimbang)",
            category = "Penenang Hati",
            arabicText = "اللَّهُمَّ إِنِّي أَسْتَخِيرُكَ بِعِلْمِكَ، وَأَسْتَقْدِرُكَ بِقُدْرَتِكَ، وَأَسْأَلُكَ مِنْ فَضْلِكَ الْعَظِيمِ، فَإِنَّكَ تَقْدِرُ وَلَا أَقْدِرُ، وَتَعْلَمُ وَلَا أَعْلَمُ، وَأَنْتَ عَلَّامُ الْغُيُوبِ",
            latinText = "Allahumma inni astakhiruka bi'ilmika, wa astaqdiruka biqudratika, wa as'aluka min fadhlikal-'azhim, fa innaka taqdiru wa la aqdiru, wa ta'lamu wa la a'lamu, wa anta 'allamul-ghuyub",
            translation = "Ya Allah, sesungguhnya aku memohon petunjuk pilihan terbaik dengan ilmu-Mu, dan aku memohon kekuatan dengan kekuasaan-Mu, dan aku memohon karunia-Mu yang agung. Sungguh Engkau Maha Kuasa dan aku tak berdaya, Engkau Maha Mengetahui dan aku tak tahu, dan Engkaulah Yang Maha Mengetahui segala hal gaib.",
            source = "HR. Bukhari no. 1162",
            benefit = "Dibaca setelah shalat sunnah Istikharah 2 rakaat ketika bingung menentukan pilihan besar dalam hidup, jodoh, karir, atau keputusan penting.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "harian_01",
            title = "Doa Bangun Tidur",
            category = "Harian",
            arabicText = "الْحَمْدُ لِلَّهِ الَّذِي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ",
            latinText = "Alhamdulillahil-ladzi ahyana ba'da ma amatana wa ilaihin-nusyur",
            translation = "Segala puji bagi Allah yang telah menghidupkan kami setelah mematikan kami (tidur), dan hanya kepada-Nya lah tempat kembali.",
            source = "HR. Bukhari no. 6312 & Muslim no. 2711",
            benefit = "Membuka hari dengan pengakuan nikmat kehidupan baru dan mengingat hari kebangkitan.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "harian_02",
            title = "Doa Sebelum Tidur & Perlindungan Malam",
            category = "Harian",
            arabicText = "بِاسْمِكَ اللَّهُمَّ أَمُوتُ وَأَحْيَا",
            latinText = "Bismikallahumma amutu wa ahya",
            translation = "Dengan nama-Mu ya Allah, aku mati dan aku hidup.",
            source = "HR. Bukhari no. 6324 & Muslim no. 2711",
            benefit = "Menyerahkan jiwa raga dalam perlindungan Allah sepanjang istirahat malam.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "harian_03",
            title = "Doa Keluar Rumah (Bismillahi Tawakkaltu)",
            category = "Perlindungan",
            arabicText = "بِسْمِ اللَّهِ تَوَكَّلْتُ عَلَى اللَّهِ، لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            latinText = "Bismillahi tawakkaltu 'alallah, la hawla wa la quwwata illa billah",
            translation = "Dengan nama Allah, aku bertawakal kepada Allah. Tiada daya dan kekuatan kecuali dengan pertolongan Allah.",
            source = "HR. Abu Dawud no. 5095 & Tirmidzi no. 3426",
            benefit = "Malaikat akan menyeru: 'Engkau telah diberi petunjuk, dicukupi kebutuhanmu, dan dilindungi', lalu setan-setan akan menjauh darimu.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "harian_04",
            title = "Doa Masuk Rumah",
            category = "Harian",
            arabicText = "بِسْمِ اللَّهِ وَلَجْنَا، وَبِسْمِ اللَّهِ خَرَجْنَا، وَعَلَى اللَّهِ رَبِّنَا تَوَكَّلْنَا",
            latinText = "Bismillahi walajna, wa bismillahi kharajna, wa 'alallahi rabbina tawakkalna",
            translation = "Dengan nama Allah kami masuk, dan dengan nama Allah kami keluar, dan kepada Allah Tuhan kami kami bertawakal.",
            source = "HR. Abu Dawud no. 5096",
            benefit = "Membawa kedamaian dan berkah ke dalam rumah serta menjauhkan godaan jin dan setan.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "harian_05",
            title = "Doa Bepergian / Naik Kendaraan",
            category = "Perlindungan",
            arabicText = "سُبْحَانَ الَّذِي سَخَّرَ لَنَا هَذَا وَمَا كُنَّا لَهُ مُقْرِنِينَ وَإِنَّا إِلَى رَبِّنَا لَمُنْقَلِبُونَ",
            latinText = "Subhanal-ladzi sakh-khara lana hadza wa ma kunna lahu muqrinin, wa inna ila rabbina lamunqalibun",
            translation = "Maha Suci Allah yang telah menundukkan semua ini bagi kami padahal kami sebelumnya tidak mampu menguasainya, dan sesungguhnya kami akan kembali kepada Tuhan kami.",
            source = "QS. Az-Zukhruf: 13-14",
            benefit = "Doa keselamatan dalam perjalanan darat, laut, maupun udara agar selamat sampai tujuan.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "quran_06",
            title = "Doa untuk Kedua Orang Tua (Kasih Sayang)",
            category = "Al-Qur'an",
            arabicText = "رَبِّ اغْفِرْ لِي وَلِوَالِدَيَّ وَارْحَمْهُمَا كَمَا رَبَّيَانِي صَغِيرًا",
            latinText = "Rabbighfir li wa liwalidayya warhamhuma kama rabbayani shaghira",
            translation = "Wahai Tuhanku, ampunilah aku dan kedua orang tuaku, serta sayangilah mereka berdua sebagaimana mereka telah mendidikku di waktu kecil.",
            source = "QS. Al-Isra: 24",
            benefit = "Bentuk birrul walidain (bakti kepada orang tua) yang menjadi salah satu amalan pembuka pintu surga.",
            recommendedCount = 3
        ),
        DoaItem(
            id = "hadist_08",
            title = "Doa Saat Tertimpa Musibah (Inna Lillahi)",
            category = "Hadist Shahih",
            arabicText = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ، اللَّهُمَّ أْجُرْنِي فِي مُصِيبَتِي، وَأَخْلِفْ لِي خَيْرًا مِنْهَا",
            latinText = "Inna lillahi wa inna ilaihi raji'un, Allahumma'jurni fi mushibati, wa akhlif li khairan minha",
            translation = "Sesungguhnya kami adalah milik Allah dan kepada-Nya lah kami kembali. Ya Allah, berilah aku pahala dalam musibahku ini, dan gantikanlah untukku dengan yang lebih baik daripadanya.",
            source = "HR. Muslim no. 918",
            benefit = "Siapa pun yang mengucapkannya dengan ikhlas, Allah pasti mengganti kesedihannya dengan kebaikan berlipat ganda.",
            recommendedCount = 1
        ),
        DoaItem(
            id = "hadist_09",
            title = "Doa Perlindungan Pagi & Petang (Bismillahil-ladzi)",
            category = "Perlindungan",
            arabicText = "بِسْمِ اللَّهِ الَّذِي لَا يَضُرُّ مَعَ اسْمِهِ شَيْءٌ فِي الْأَرْضِ وَلَا فِي السَّمَاءِ وَهُوَ السَّمِيعُ الْعَلِيمُ",
            latinText = "Bismillahil-ladzi la yadhurru ma'asmihi syai'un fil-ardhi wa la fis-sama'i wa huwas-sami'ul-'alim",
            translation = "Dengan nama Allah yang bersama nama-Nya tidak ada sesuatu pun di bumi maupun di langit yang dapat mendatangkan bahaya, dan Dia Maha Mendengar lagi Maha Mengetahui.",
            source = "HR. Abu Dawud no. 5088 & Tirmidzi no. 3388",
            benefit = "Barangsiapa membacanya 3 kali di pagi dan petang hari, niscaya tidak akan ada mara bahaya atau racun/sihir yang mencelakainya.",
            recommendedCount = 3
        ),
        DoaItem(
            id = "hadist_10",
            title = "Doa Pelunas Hutang yang Menumpuk",
            category = "Rezeki",
            arabicText = "اللَّهُمَّ اكْفِنِي بِحَلَالِكَ عَنْ حَرَامِكَ، وَأَغْنِنِي بِفَضْلِكَ عَمَّنْ سِوَاكَ",
            latinText = "Allahummakfini bihalalika 'an haramik, wa aghnini bifadhlika 'amman siwak",
            translation = "Ya Allah, cukupkanlah aku dengan rezeki-Mu yang halal dari yang haram, dan kayakanlah aku dengan karunia-Mu dari selain diri-Mu.",
            source = "HR. Tirmidzi no. 3563 & Ahmad",
            benefit = "Ali bin Abi Thalib RA menyatakan doa ini mampu melunasi hutang walau sebesar gunung Shirr berkat pertolongan Allah.",
            recommendedCount = 3
        )
    )

    fun getDoasFlow(): Flow<List<DoaItem>> {
        return bookmarkDao.getAllBookmarks().combine(customAndAiDoas) { bookmarks, aiDoas ->
            val bookmarkedIds = bookmarks.map { it.doaId }.toSet()
            val allList = initialDoas + aiDoas
            allList.map { doa ->
                doa.copy(isBookmarked = bookmarkedIds.contains(doa.id))
            }
        }
    }

    suspend fun toggleBookmark(doaId: String) {
        if (bookmarkDao.isBookmarked(doaId)) {
            bookmarkDao.deleteBookmark(doaId)
        } else {
            bookmarkDao.insertBookmark(DoaBookmarkEntity(doaId = doaId))
        }
    }

    fun addCustomAiDoa(item: DoaItem) {
        val current = customAndAiDoas.value.toMutableList()
        if (current.none { it.id == item.id }) {
            current.add(0, item)
            customAndAiDoas.value = current
        }
    }
}
