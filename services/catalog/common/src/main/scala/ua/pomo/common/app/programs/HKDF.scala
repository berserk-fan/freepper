package ua.pomo.common.app.programs

import cats.MonadThrow

import java.security.{InvalidKeyException, Key, NoSuchAlgorithmException}
import java.util
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Android Sync Client.
 *
 * The Initial Developer of the Original Code is
 * the Mozilla Foundation.
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 * Jason Voll
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

/*
 * A standards-compliant implementation of RFC 5869
 * for HMAC-based Key Derivation Function.
 * HMAC uses HMAC SHA256 standard.
 */
object HKDF {

  /** Used for conversion in cases in which you *know* the encoding exists.
    */
  private def bytes(in: String): Array[Byte] = {
    in.getBytes("UTF-8")
  }

  private val BLOCKSIZE: Int = 256 / 8

  /* uses hmac-sha256 and utf-8 encoding */
  def hkdf[F[_]: MonadThrow](key: String, salt: String, info: String, len: Int): F[Array[Byte]] =
    MonadThrow[F].catchNonFatal {
      val PRK = hkdfExtract(bytes(salt), bytes(key))
      hkdfExpand(PRK, bytes(info), len)
    }

  /*
   * Step 1 of RFC 5869
   * Get sha256HMAC Bytes
   * Input: salt (message), IKM (input keyring material)
   * Output: PRK (pseudorandom key)
   */
  private def hkdfExtract(salt: Array[Byte], IKM: Array[Byte]): Array[Byte] = digestBytes(IKM, makeHMACHasher(salt))

  /*
   * Step 2 of RFC 5869.
   * Input: PRK from step 1, info, length.
   * Output: OKM (output keyring material).
   */
  private def hkdfExpand(prk: Array[Byte], info: Array[Byte], len: Int): Array[Byte] = {
    val hmacHasher = makeHMACHasher(prk)
    var T = Array[Byte]()
    var Tn = Array[Byte]()
    val iterations = Math.ceil(len.toDouble / BLOCKSIZE.toDouble).toInt
    for (i <- 0 until iterations) {
      Tn = digestBytes(Utils.concatAll(Tn, info, Utils.hex2Byte(Integer.toHexString(i + 1))), hmacHasher)
      T = Utils.concatAll(T, Tn)
    }
    util.Arrays.copyOfRange(T, 0, len)
  }

  /*
   * Make HMAC key
   * Input: key (salt)
   * Output: Key HMAC-Key
   */
  private def makeHMACKey(key1: Array[Byte]): Key = {
    var key = key1
    if (key.length == 0) {
      key = new Array[Byte](BLOCKSIZE)
    }
    new SecretKeySpec(key, "HmacSHA256")
  }

  /*
   * Make an HMAC hasher
   * Input: Key hmacKey
   * Ouput: An HMAC Hasher
   */
  private def makeHMACHasher(key: Array[Byte]): Mac = {
    var hmacHasher: Mac =
      try {
        Mac.getInstance("hmacSHA256")
      } catch {
        case e: NoSuchAlgorithmException =>
          e.printStackTrace()
          null
      }
    try {
      hmacHasher.init(makeHMACKey(key))
    } catch {
      case e: InvalidKeyException =>
        e.printStackTrace()
    }
    hmacHasher
  }

  /*
   * Hash bytes with given hasher
   * Input: message to hash, HMAC hasher
   * Output: hashed byte[].
   */
  private def digestBytes(message: Array[Byte], hasher: Mac): Array[Byte] = {
    hasher.update(message)
    val ret = hasher.doFinal()
    hasher.reset()
    ret
  }

  private object Utils {
    /*
     * Helper to convert Hex String to Byte Array
     * Input: Hex string
     * Output: byte[] version of hex string
     */
    def hex2Byte(str1: String): Array[Byte] = {
      var str = str1
      if (str.length % 2 == 1) {
        str = "0" + str
      }
      val bytes = new Array[Byte](str.length / 2)
      for (i <- bytes.indices) {
        bytes(i) = Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16).toByte
      }
      bytes
    }

    /*
     * Helper for array concatenation.
     * Input: At least two byte[]
     * Output: A concatenated version of them
     */
    def concatAll(first: Array[Byte], rest: Array[Byte]*): Array[Byte] = {
      var totalLength = first.length
      for (array <- rest) {
        totalLength += array.length
      }
      val result = util.Arrays.copyOf(first, totalLength)
      var offset = first.length
      for (array <- rest) {
        System.arraycopy(array, 0, result, offset, array.length)
        offset += array.length
      }
      result
    }
  }
}
