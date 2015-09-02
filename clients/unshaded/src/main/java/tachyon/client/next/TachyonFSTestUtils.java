/*
 * Licensed to the University of California, Berkeley under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package tachyon.client.next;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tachyon.TachyonURI;
import tachyon.client.next.file.FileOutStream;
import tachyon.client.next.file.TachyonFileSystem;
import tachyon.client.next.file.TachyonFile;
import tachyon.conf.TachyonConf;
import tachyon.thrift.FileInfo;

public final class TachyonFSTestUtils {
  /**
   * Create a simple file with <code>len</code> bytes.
   *
   * @param tfs a TachyonFS handler
   * @param fileName the name of the file to be created
   * @param options client options to create the file with
   * @param len file size in bytes
   * @return the TachyonFile representation of the created file
   * @throws IOException if <code>path</code> is invalid (e.g., illegal URI)
   */
  public static TachyonFile createByteFile(TachyonFileSystem tfs, String fileName,
      ClientOptions options, int len) throws IOException {
    return createByteFile(tfs, fileName, options.getCacheType(), options.getUnderStorageType(),
        len, options.getBlockSize());
  }

  /**
   * Create a simple file with <code>len</code> bytes.
   *
   * @param tfs a TachyonFS handler
   * @param fileName the name of the file to be created
   * @param cacheType CacheType used to create the file
   * @param underStorageType UnderStorageType used to create the file
   * @param len file size
   * @return the TachyonFile of the created file
   * @throws IOException if <code>path</code> is invalid (e.g., illegal URI)
   */
  public static TachyonFile createByteFile(TachyonFileSystem tfs, String fileName,
      CacheType cacheType, UnderStorageType underStorageType, int len) throws IOException {
    return createByteFile(tfs, new TachyonURI(fileName), cacheType, underStorageType, len);
  }

  /**
   * Create a simple file with <code>len</code> bytes.
   *
   * @param tfs a TachyonFS handler
   * @param fileURI URI of the file
   * @param cacheType CacheType used to create the file
   * @param underStorageType UnderStorageType used to create the file
   * @param len file size
   * @return the TachyonFile of the created file
   * @throws IOException if <code>path</code> is invalid (e.g., illegal URI)
   */
  public static TachyonFile createByteFile(TachyonFileSystem tfs, TachyonURI fileURI,
      CacheType cacheType, UnderStorageType underStorageType, int len) throws IOException {
    ClientOptions options =
        new ClientOptions.Builder(new TachyonConf()).setCacheType(cacheType)
            .setUnderStorageType(underStorageType).build();
    FileOutStream os = tfs.getOutStream(fileURI, options);

    for (int k = 0; k < len; k ++) {
      os.write((byte) k);
    }
    os.close();
    return tfs.open(fileURI);
  }

  /**
   * Create a simple file with <code>len</code> bytes.
   *
   * @param tfs a TachyonFS handler
   * @param fileName the name of the file to be created
   * @param cacheType CacheType used to create the file
   * @param underStorageType UnderStorageType used to create the file
   * @param len file size
   * @param blockCapacityByte block size of the file
   * @return the TachyonFile of the created file
   * @throws IOException if <code>path</code> is invalid (e.g., illegal URI)
   */
  public static TachyonFile createByteFile(TachyonFileSystem tfs, String fileName,
      CacheType cacheType, UnderStorageType underStorageType, int len, long blockCapacityByte)
      throws IOException {
    ClientOptions options =
        new ClientOptions.Builder(new TachyonConf()).setCacheType(cacheType)
            .setUnderStorageType(underStorageType).setBlockSize(blockCapacityByte).build();
    FileOutStream os = tfs.getOutStream(new TachyonURI(fileName), options);

    for (int k = 0; k < len; k ++) {
      os.write((byte) k);
    }
    os.close();
    return tfs.open(new TachyonURI(fileName));
  }

  /**
   * List files at a given <code>path</code>.
   *
   * @param tfs a TachyonFS handler
   * @param path a path in tachyon file system
   * @return a list of stings representing the file names under the given path
   * @throws IOException if <code>path</code> does not exist or is invalid
   */
  public static List<String> listFiles(TachyonFileSystem tfs, String path) throws IOException {
    List<FileInfo> infos = tfs.listStatus(tfs.open(new TachyonURI(path)));
    List<String> res = new ArrayList<String>();
    for (FileInfo info : infos) {
      res.add(info.getPath());

      if (info.isFolder) {
        res.addAll(listFiles(tfs, info.getPath()));
      }
    }

    return res;
  }

}