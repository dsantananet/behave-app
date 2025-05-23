(ns file-utils.core
  (:require [clojure.java.io :as io]
            [clojure.string  :as str]
            [me.raynes.fs    :as fs])
  (:import [java.io File OutputStream ByteArrayInputStream ByteArrayOutputStream]
           [javax.imageio ImageIO]
           [java.awt.image BufferedImage]
           [java.util.zip ZipEntry ZipOutputStream ZipInputStream]
           [net.coobird.thumbnailator Thumbnails]))

;;; Constants

(def ^:private image-extensions #{".png" ".jpg" ".jpeg" ".svg" ".webp" ".tiff"})
(def ^:private resize-max-width 500)
(def ^:private resize-quality   0.8)

;;; Image Resizing

(defn- write-image [^BufferedImage image format-name ^OutputStream output-stream]
  (ImageIO/write image format-name output-stream))

(defn- write-image-to-file [^BufferedImage image format-name ^File output-file]
  (ImageIO/write image format-name output-file))

(defn resize-image [file image-max-size image-quality & [output-file]]
  (let [image-quality  (or image-quality 1.0)
        output-stream  (ByteArrayOutputStream.)
        format         (subs (fs/extension file) 1)
        buffered-image (-> (doto (Thumbnails/fromFilenames [(.getPath file)])
                             (.size image-max-size image-max-size)
                             (.outputQuality image-quality))
                           (.asBufferedImage))]
    (try 
      (if output-file
        (write-image-to-file buffered-image format output-file)
        (write-image buffered-image format output-stream))
      (finally (.close output-stream)))
    output-stream))

;; Adapted from: https://gist.github.com/mikeananev/b2026b712ecb73012e680805c56af45f
(defn zip-file
  "compress file or folder
  `input-file-or-folder` - filename or folder to be compressed.
  `out-file` - filename of output archive
  `opts` - optionals map, which can take:
    - `resize-images?` - resizes all images
    - `image-max-size` - resizes images to this max height/width, keeping the aspect ratio (default: 500)
    - `image-quality`  - compresses images to this quality (default: 0.8)
    - `rel-path?`      - replaces the path of each zip entry with the relative path
    - `new-path`       - string which replaces the path of each file with `new-path`,
                         or a function which takes the original path and outputs a new path."
  [input-file-or-folder out-file & [{:keys [rel-path? new-path resize-images? image-max-size image-quality]
                                     :or {image-max-size resize-max-width image-quality resize-quality}}]]
  (with-open [zip (ZipOutputStream. (io/output-stream out-file))]
    (doseq [f (file-seq (io/file input-file-or-folder)) :when (.isFile f)]
      (.putNextEntry zip (ZipEntry. (cond
                                      (string? new-path) (str new-path (fs/base-name f))
                                      (fn? new-path)     (new-path (.getPath f))
                                      rel-path?          (str/replace (.getPath f) (.getPath (System/getProperty "user.dir")) ".")
                                      :else              (.getPath f))))
      (if (and resize-images? (image-extensions (fs/extension f)))
        (io/copy (ByteArrayInputStream. (.toByteArray (resize-image f image-max-size image-quality))) zip)
        (io/copy f zip))
      (.closeEntry zip))))

(defn unzip-file
  "uncompress zip archive.
  `input` - name of zip archive to be uncompressed.
  `output` - name of folder where to output."
  [input output]
  (with-open [stream (-> input io/input-stream ZipInputStream.)]
    (loop [entry (.getNextEntry stream)]
      (when entry
        (let [save-path (str output File/separatorChar (.getName entry))
              out-file  (File. save-path)]
          (if (.isDirectory entry)
            (when-not (.exists out-file)
              (.mkdirs out-file))
            (let [parent-dir (File. (.substring save-path 0 (.lastIndexOf save-path (int File/separatorChar))))]
              (when-not (.exists parent-dir) (.mkdirs parent-dir))
              (io/copy stream out-file)))
          (recur (.getNextEntry stream)))))))

(defn resource-file
  "Returns the file of a resource (safe for JAR files)."
  [path]
  (io/file (.getFile (io/resource path))))

(defn os-path
  "Translates a path in either Windows/Unix format into a path compatible with the current system."
  [path]
  (-> path
      (str/replace "~" (str (fs/home)))
      (str/replace "$HOME" (str (fs/home)))
      (str/split #"[/\\]")
      (->> (apply fs/file))
      (str)))
