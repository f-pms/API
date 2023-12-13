package com.hbc.pms.plc.io;

import java.util.List;

public interface IoDetailsRepository {
  void add(IoCoordinates ioCoordinates);

  IoCoordinates get(String id);

  List<IoCoordinates> getAll();
}
