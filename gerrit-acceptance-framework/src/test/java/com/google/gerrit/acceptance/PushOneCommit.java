    this(notesFactory, approvalsUtil, queryProvider, db, i, testRepo, subject, files, null);
      assertReviewers(c, expectedReviewers);
    private void assertReviewers(Change c, TestAccount... expectedReviewers) throws OrmException {
          approvalsUtil.getReviewers(db, notesFactory.createChecked(db, c)).all();