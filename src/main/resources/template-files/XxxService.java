package BASE_PACKAGE_NAME.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class XxxService {

    private static Logger logger = LoggerFactory.getLogger(XxxService.class);

    @Autowired
    private XxxRepo xxxRepo;

    @Autowired
    private XxxRepoCustom xxxRepoCustom;


    @Transactional
    public Xxx create(CreateXxxReq req)
    {


        logger.info("A person with id {} and type {} was created.", createdPerson.getId(), createdPerson.getType());
        return createdPerson;
    }

    private void persistBoardMembers(List<BoardMember> boardMembers, long legalPersonId) {

        logger.info("persisting board members for legal person with id {}", legalPersonId);
        deleteCurrentBoardMembers(legalPersonId);

        if(boardMembers == null)
            return;

        for(BoardMember bm: boardMembers) {
            BoardMember toBeAddedBM = new BoardMember();
            toBeAddedBM.setCorporation(new LegalPerson(legalPersonId));
            toBeAddedBM.setPerson(findRealById(bm.getPerson().getId()));
            toBeAddedBM.setTitle(bm.getTitle());
            BoardMember createdBM = boardMemberRepo.save(toBeAddedBM);
            logger.info("Board member with id {} was created.", createdBM.getId());
        }

    }



    public void confirm(long personId) {
        Person person = findById(personId);
//		if(person.getStatus() == PersonStatus.CONFIRMED)
//			throw new ConfirmingConfirmedPersonException();

        if(person.getType() == PersonType.LEGAL)
            confirmBoardMembers(person);
        person.setStatus(PersonStatus.CONFIRMED);
        personRepo.save(person);
        logger.info("Person with ID {} was confirmed", personId);
    }

    private void confirmBoardMembers(Person person) {
        LegalPerson legalPerson = (LegalPerson) person;
        for(BoardMember boardMember: legalPerson.getBoardMembers())
        {
            RealPerson realPerson = boardMember.getPerson();
            realPerson.setStatus(PersonStatus.CONFIRMED);
            personRepo.save(realPerson);
        }

    }

    private Person findById(long personId)
    {

        Optional<Person> optPerson = personRepo.findById(personId);
        if(optPerson.isEmpty())
            throw new EntityNotFoundException(personId, null);

        return optPerson.get();
    }

    @Transactional
    public Person update(long personId, Person person) {
        Person existingPerson = findById(personId);

        if(existingPerson.getStatus() == PersonStatus.CONFIRMED)
            throw new UpdatingConfirmedPersonException();

        String uniqueCode = PersonUniqueCodeCalculator.calculate(person);
        if(!existingPerson.getUniqueCode().equals(uniqueCode))
            throw new UpdatingPersonUniqueCodeException(existingPerson.getUniqueCode());

        setFieldsThatShouldNotChangeOnUpdate(person, existingPerson);
        person.setLastEditDate(new Date());

        List<BoardMember> boardMembers = null;
        if(person.getType() == PersonType.LEGAL)
        {
            LegalPerson legalPerson = (LegalPerson)person;
            boardMembers = legalPerson.getBoardMembers();
            persistBoardMembers(boardMembers, existingPerson.getId());
            legalPerson.setBoardMembers(null);
        }

        Person updatedPerson = personRepo.save(person);

        if(person.getType() == PersonType.LEGAL)
        {
            ((LegalPerson)updatedPerson).setBoardMembers(boardMembers); //set again to be used in result
        }

        logger.info("person with id {} and type {} was updated.", existingPerson.getId(), updatedPerson.getType());

        return updatedPerson;
    }

    private void deleteCurrentBoardMembers(long legalPersonId) {
        logger.info("Deleting current board members of legal person with id {}", legalPersonId);
        LegalPerson legalPerson = findLegalById(legalPersonId);
        if(legalPerson.getBoardMembers() == null)
            return;
        for(BoardMember bm: legalPerson.getBoardMembers()) {
            logger.info("deleting board member with id {}", bm.getId());
            boardMemberRepo.delete(bm);
        }
    }

    private void setFieldsThatShouldNotChangeOnUpdate(Person sentByUserPerson, Person existingPerson) {
        sentByUserPerson.setId(existingPerson.getId());
        sentByUserPerson.setCreationDate(existingPerson.getCreationDate());
        sentByUserPerson.setUniqueCode(existingPerson.getUniqueCode());
        sentByUserPerson.setStatus(existingPerson.getStatus());
        sentByUserPerson.setVersion(existingPerson.getVersion());
        sentByUserPerson.setCurrentVersion(existingPerson.isCurrentVersion());
        if(sentByUserPerson.getType() == PersonType.REAL)
            ((RealPerson)sentByUserPerson).setPersonalImageDocumentId(((RealPerson)existingPerson).getPersonalImageDocumentId());
    }

    public Page<RealPerson> searchReal(SearchDto<RealPersonSearchFilter> req) {
        logger.info("Searching real persons");
        return realPersonRepoCustom.findByFilter(req);
    }

    public Page<LegalPerson> searchLegal(SearchDto<LegalPersonSearchFilter> req) {
        logger.info("Searching legal persons");
        return legalPersonRepoCustom.findByFilter(req);
    }

    public RealPerson inquireSabteAhval(SabteAhvalInquireReq req) {
        validateSabteAhvalInquireReq(req);

        SabteAhvalClient client = new SabteAhvalClient();
        client.setLoginUrl(parameterService.getSabteAhvalLoginUrl());
        client.setShowInfoUrl(parameterService.getSabteAhvalShowInfoUrl());
        client.setUsername(parameterService.getSabteAhvalUserName());
        client.setPassword(parameterService.getSabteAhvalPassword());
        Optional<RealPerson> optPerson = client.inquire(req.getNationalCode(), req.getBirthDate());
        if(optPerson.isEmpty())
            return null;

        RealPerson person = optPerson.get();
        person.setIranian(true);
        person.setUniqueCode(PersonUniqueCodeCalculator.calculate(person));
        return person;
    }

    private void validateSabteAhvalInquireReq(SabteAhvalInquireReq req) {
        if(Validations.isBlank(req.getNationalCode()))
            throw new InvalidInputException("national code is required");

        if(req.getNationalCode().length() != 10)
            throw new InvalidInputException("National code length must be 10");

//		if(req.getBirthDate() == null)
//			throw new InvalidInputException("Birth date is required");

    }


    private RealPerson findRealById(long id) {
        Person realPerson = findById(id);
        if(realPerson.getType() != PersonType.REAL)
            throw new InvalidInputException("person with id "+id+" is not an instance of REAL person.");
        return (RealPerson)realPerson;
    }

    private LegalPerson findLegalById(long id) {
        Person corporation = findById(id);
        if(corporation.getType() != PersonType.LEGAL)
            throw new InvalidInputException("person with id "+id+" is not an instance of LEGAL person.");
        return (LegalPerson)corporation;
    }

    public RealPersonComparisonResult compareRealWithDb(RealPerson givenPerson) {
        logger.info("comparing real person with national code {} with DB", givenPerson.getUniqueCode());
        Person dbPerson = personRepo.findByUniqueCodeAndCurrentVersion(givenPerson.getUniqueCode(), true);

        if(dbPerson == null)
            return new RealPersonComparisonResult(true);

        if(!(dbPerson instanceof RealPerson))
            throw new InvalidInputException("given person unique code doesn't belong to a real person");

        return new PersonComparer().compareReal(givenPerson, (RealPerson)dbPerson);
    }

    public RealPersonComparisonResult compareReal(RealPerson person1, RealPerson person2) {
        logger.info("comparing two real persons");
        return new PersonComparer().compareReal(person1, person2);
    }

    public LegalPersonComparisonResult compareLegal(LegalPerson person1, LegalPerson person2) {
        logger.info("comparing two legal persons");
        return new PersonComparer().compareLegal(person1, person2);
    }

    public LegalPersonComparisonResult compareLegalWithDb(LegalPerson givenPerson) {
        logger.info("Comparing legal person with unique code {} with DB", givenPerson.getUniqueCode());
        Person dbPerson = personRepo.findByUniqueCodeAndCurrentVersion(givenPerson.getUniqueCode(), true);

        if(dbPerson == null)
            return new LegalPersonComparisonResult(true);

        if(!(dbPerson instanceof LegalPerson))
            throw new InvalidInputException("given person unique code doesn't belong to a legal person");

        LegalPersonComparisonResult result = new PersonComparer().compareLegal(givenPerson, (LegalPerson)dbPerson);
        return result;
    }

//	public void resolveRealConflicts(long personId, RealPersonComparisonResult req) {
//		logger.info("Resolving real person with id {} conflicts", personId);
//		RealPerson dbPerson = findRealById(personId);
//		for(PersonFieldConflict<RealPersonField> conflict: req.getConflicts()) {
//			if(conflict.getSelected().equals("first"))
//				PersonFieldResolver.resolveRealField(conflict.getField(), conflict.getFirstValue(), dbPerson);
//		}
//		update(personId, dbPerson);
//	}

//	@Transactional
//	public void resolveLegalConflicts(long personId, LegalPersonComparisonResult req) {
//		logger.info("Resolving legal person with id {} conflicts", personId);
//		//second value is db person - look at compare func
//		LegalPerson dbPerson = findLegalById(personId);
//		//resolve legal person field conflicts
//		for(PersonFieldConflict<LegalPersonField> conflict: req.getConflicts()) {
//			if(conflict.getSelected().equals("first")) //want to use req value
//				PersonFieldResolver.resolveLegalField(conflict.getField(), conflict.getFirstValue(), dbPerson);
//		}
//
//		//resolve board member conflicts
//		for(BoardMemberComparisonResult memberConflict: req.getCommonBoardMemberConflicts()) {
//			BoardMember boardMember = findMember(memberConflict.getPersonComparisonResult().getSecondPersonId(), dbPerson);
//			if(memberConflict.getSelected() != null && memberConflict.getSelected().equals("first"))
//				boardMember.setTitle(memberConflict.getFirstTitleValue());
//			for(PersonFieldConflict<RealPersonField> conflict: memberConflict.getPersonComparisonResult().getConflicts()) {
//				PersonFieldResolver.resolveRealField(conflict.getField(), conflict.getFirstValue(), boardMember.getPerson());
//			}
//		}
//
//		//add board members that not exist in db
//		prepareReqFirstBoardMembers(req.getFirstPersonBoardMembers());
//		dbPerson.getBoardMembers().addAll(req.getFirstPersonBoardMembers());
//
//		update(personId, dbPerson);
//	}



//	private void prepareReqFirstBoardMembers(List<BoardMember> firstPersonBoardMembers) {
//		for(BoardMember bm: firstPersonBoardMembers) {
//			bm.setId(0);
//			bm.getPerson().setId(0);
//			RealPerson existingPerson = findRealByNationalCode(bm.getPerson().getNationalCode());
//			if(existingPerson != null)
//				//we assume that the conflicts were resolved before (in WUI logic)
//				//so existing person in db is OK
//				bm.setPerson(existingPerson);
//			else
//				bm.setPerson((RealPerson)create(bm.getPerson()));
//		}
//
//	}

//	private BoardMember findMember(long memberPersonId, LegalPerson person) {
//		for(BoardMember bm: person.getBoardMembers()) {
//			if(bm.getPerson().getId() == memberPersonId)
//				return bm;
//		}
//		return null;
//	}

    private RealPerson findRealByNationalCode(String nationalCode) {
        Person person = personRepo.findByUniqueCodeAndCurrentVersion(nationalCode, true);
        if(person == null)
            return null;
        if(!(person instanceof RealPerson))
            throw new InternalErrorException("Searching by national code " + nationalCode + " but found legal person! (not real)", null);

        return (RealPerson)person;
    }

    public RealPersonComparisonResult compareRealWithSabteAhval(RealPerson givenPerson) {
        logger.info("Comparing real person with national code {} with sabte ahval", givenPerson.getUniqueCode());
        RealPerson person = new RealPerson();
        person.setFirstName(givenPerson.getFirstName());
        person.setLastName(givenPerson.getLastName());
        person.setNationalCode(givenPerson.getNationalCode());

        //note that sabte ahval returns 0 if national code equals to id number
        if(givenPerson.getNationalCode().equals(givenPerson.getIdNumber()))
            person.setIdNumber("0");
        else
            person.setIdNumber(givenPerson.getIdNumber());

        person.setFatherName(givenPerson.getFatherName());
        person.setBirthPlace(givenPerson.getBirthPlace());
        person.setBirthDate(givenPerson.getBirthDate());
        person.setIranian(true);

        SabteAhvalInquireReq sabteAhvalReq = new SabteAhvalInquireReq();
        sabteAhvalReq.setNationalCode(givenPerson.getNationalCode());
        RealPerson sabteAhvalPerson = inquireSabteAhval(sabteAhvalReq);
        if(sabteAhvalPerson == null)
            return new RealPersonComparisonResult(false);

        return new PersonComparer().compareReal(person, sabteAhvalPerson);
    }

    @Transactional
    public Person createOrUpdate(Person person) {
        String uniqueCode = PersonUniqueCodeCalculator.calculate(person);
        Person existingPerson = personRepo.findByUniqueCodeAndCurrentVersion(uniqueCode, true);
        if(existingPerson == null || existingPerson.getStatus() == PersonStatus.CONFIRMED) {
            logger.info("Person with unique code {} does not exists or exists with confirmed status. So creating a new person.", person.getUniqueCode());
            return create(person);
        } else {
//			logger.info("Person with unique code {} exists with unconfirmed status. So first confirming existing person and then create new one.", person.getUniqueCode());
//			confirm(existingPerson.getId());
            logger.info("Person with unique code {} exists with unconfirmed status. So updating...", person.getUniqueCode());
            return update(existingPerson.getId(), person);
        }
    }

    public Person get(long id) {
        logger.info("Getting person with id {}", id);
        Person person = findById(id);
        Optional<PersonGeneralInfo> optGeneralInfo = personGeneralInfoRepo.findById(person.getUniqueCode());
        if(optGeneralInfo.isPresent())
            person.setGeneralInfo(optGeneralInfo.get());
        return person;
    }

    public AssignPhysicalFileNumberResp assignPhysicalFileNumber(long personId, String physicalFileNumber,
                                                                 PhysicalFileLocation physicalFileLocation) {
        logger.info("Assigning physical file number for person with id {}", personId);

        AssignPhysicalFileNumberResp resp = new AssignPhysicalFileNumberResp();
        PersonGeneralInfo toBeSavedGeneralInfo = new PersonGeneralInfo();

        PhysicalFileLocation finalPhysicalFileLocation = physicalFileLocation == null ? PhysicalFileLocation.HIT_CYBER_DEFENSE_CIRCLE : physicalFileLocation;
        String finalPhysicalFileNumber = determinePhysicalFileNumber(finalPhysicalFileLocation, physicalFileNumber);

        Person person = findById(personId);

        Optional<PersonGeneralInfo> optGeneralInfo = personGeneralInfoRepo.findById(person.getUniqueCode());
        if(optGeneralInfo.isEmpty()) {
            toBeSavedGeneralInfo.setUniqueCode(person.getUniqueCode());
            toBeSavedGeneralInfo.setPhysicalFileLocation(finalPhysicalFileLocation);
            toBeSavedGeneralInfo.setPhysicalFileNumber(finalPhysicalFileNumber);
        } else {
            toBeSavedGeneralInfo = optGeneralInfo.get();
            toBeSavedGeneralInfo.setPhysicalFileLocation(finalPhysicalFileLocation);
            toBeSavedGeneralInfo.setPhysicalFileNumber(finalPhysicalFileNumber);
        }
        personGeneralInfoRepo.save(toBeSavedGeneralInfo);

        resp.setPhysicalFileLocation(toBeSavedGeneralInfo.getPhysicalFileLocation());
        resp.setPhysicalFileNumber(toBeSavedGeneralInfo.getPhysicalFileNumber());
        return resp;
    }

    private String determinePhysicalFileNumber(PhysicalFileLocation finalPhysicalFileLocation,
                                               String physicalFileNumber) {

        String result = physicalFileNumber;

        if(Validations.isBlank(result)) {
            List<PersonGeneralInfo> files = personGeneralInfoRepo
                    .findByPhysicalFileLocationOrderByPhysicalFileNumber(finalPhysicalFileLocation);

            if(files.size() == 0)
                return "01-01-01";

            result = PhysicalFileNumberHelper.calculateNext(
                    files.get(files.size() - 1).getPhysicalFileNumber());
        }

        physicalFileNumber = physicalFileNumber.trim();

        //check uniqueness
        PersonGeneralInfo generalInfo = personGeneralInfoRepo.findByPhysicalFileNumberAndPhysicalFileLocation(physicalFileNumber, finalPhysicalFileLocation);
        if(generalInfo != null)
            throw new DuplicatePhysicalFileNumberException(physicalFileNumber);

        return result;
    }

    public void setNdaStatus(long personId, boolean ndaSigned) {
        logger.info("Setting person with ID {} NDA status to {}", personId, ndaSigned);

        Person person = findById(personId);

        PersonGeneralInfo toBeSavedPersonGeneralInfo = null;

        Optional<PersonGeneralInfo> optGeneralInfo = personGeneralInfoRepo.findById(person.getUniqueCode());

        if(optGeneralInfo.isPresent()) {
            toBeSavedPersonGeneralInfo = optGeneralInfo.get();
        } else {
            toBeSavedPersonGeneralInfo = new PersonGeneralInfo();
            toBeSavedPersonGeneralInfo.setUniqueCode(person.getUniqueCode());
        }

        toBeSavedPersonGeneralInfo.setNdaSigned(ndaSigned);

        personGeneralInfoRepo.save(toBeSavedPersonGeneralInfo);
    }

    public List<VersionInfo> getVersions(String uniqueCode) {
        List<VersionInfo> result = new ArrayList<>();
        List<Person> persons = personRepo.findByUniqueCode(uniqueCode);
        for(Person person: persons) {
            VersionInfo v = new VersionInfo();
            v.setCreationDate(person.getCreationDate());
            v.setCurrentVersion(person.isCurrentVersion());
            v.setLastEditDate(person.getLastEditDate());
            v.setStatus(person.getStatus());
            v.setVersion(person.getVersion());
            v.setPersonId(person.getId());
            result.add(v);
        }
        return result;
    }

    public List<BoardMemberInfo> getRelatedBoardMembers(String uniqueCode) {
        List<BoardMemberInfo> boardMemberInfos = new ArrayList<>();
        List<Person> persons = personRepo.findByUniqueCode(uniqueCode);

        for(Person person: persons) {
            List<BoardMember> members = boardMemberRepo.findAllByPerson((RealPerson)person);
            for(BoardMember bm: members) {
                BoardMemberInfo info = new BoardMemberInfo();
                info.setCorporationId(bm.getCorporation().getId());
                info.setCorporationName(bm.getCorporation().getName());
                info.setPersonId(person.getId());
                info.setTitle(bm.getTitle());
                boardMemberInfos.add(info);
            }
        }

        return boardMemberInfos;
    }

    public List<RelatedPersonDto> getLegalRelatedPersons(String uniqueCode) {
        Set<String> addedUniqueCodes = new HashSet<>();
        List<Person> corps = personRepo.findByUniqueCode(uniqueCode);
        List<RelatedPersonDto> relatedPersons = new ArrayList<>();

        //adding all board members (for all legal person versions)
        for(Person corp: corps) {
            LegalPerson legalPerson = (LegalPerson)corp;
            List<BoardMember> boardMembers = legalPerson.getBoardMembers();
            for(BoardMember bm: boardMembers) {
                String key = bm.getPerson().getUniqueCode()+":"+bm.getTitle();
                if(addedUniqueCodes.contains(key))
                    continue;
                relatedPersons.add(makeRpObject(bm));
                addedUniqueCodes.add(key);
            }

            //adding persons who works in corp
            List<RealPerson> employees = realPersonRepoCustom.findByJobLocationId(corp.getId());
            for(RealPerson employee: employees) {
                JobInfo jobInfo = getRelatedJobInfo(employee, corp.getId());
                String key = employee.getUniqueCode()+":"+jobInfo.getPosition();
                if(addedUniqueCodes.contains(key))
                    continue;
                relatedPersons.add(makeRpObject(employee, jobInfo));
                addedUniqueCodes.add(key);
            }

        }

        return relatedPersons;
    }

    private RelatedPersonDto makeRpObject(RealPerson employee, JobInfo jobInfo) {
        RelatedPersonDto rp = new RelatedPersonDto();
        rp.setFirstName(employee.getFirstName());
        rp.setLastName(employee.getLastName());
        rp.setPersonId(employee.getId());
        rp.setNationalCode(employee.getNationalCode());
        rp.setTitle(jobInfo.getPosition());
        rp.setFromDate(jobInfo.getFromDate());
        rp.setToDate(jobInfo.getToDate());
        rp.setDescription(jobInfo.getDescription());
        return rp;
    }

    private RelatedPersonDto makeRpObject(BoardMember bm) {
        RelatedPersonDto rp = new RelatedPersonDto();
        rp.setFirstName(bm.getPerson().getFirstName());
        rp.setLastName(bm.getPerson().getLastName());
        rp.setTitle(bm.getTitle());
        rp.setPersonId(bm.getPerson().getId());
        rp.setNationalCode(bm.getPerson().getNationalCode());
        return rp;
    }

    private JobInfo getRelatedJobInfo(RealPerson employee, long jobLocationId) {
        return employee.getJobInfos().stream()
                .filter(item -> item.getJobLocationId().equals(jobLocationId)).findFirst().get();
    }

    public SyncWithSabteAhvalResult syncWithSabteAhval(long personId) {
        SyncWithSabteAhvalResult result = new SyncWithSabteAhvalResult();
        RealPerson person = findRealById(personId);
        if(person.getStatus() != PersonStatus.CREATED)
            throw new InvalidInputException("Syncing with sabte ahval is only possible in CREATED state");

        RealPersonComparisonResult comparisonResult = compareRealWithSabteAhval(person);

        for(PersonFieldConflict<RealPersonField> conflict: comparisonResult.getConflicts()) {
            switch(conflict.getField()) {
                case FIRST_NAME:
                    person.setFirstName(conflict.getSecondValue());
                    break;
                case LAST_NAME:
                    person.setLastName(conflict.getSecondValue());
                    break;
                case FATHER_NAME:
                    person.setFatherName(conflict.getSecondValue());
                    break;
                case ID_NUMBER:
                    person.setIdNumber(conflict.getSecondValue());
                    break;
                case BIRTH_DATE:
                    DateConverter dc = new DateConverter();
                    String[] splittedDate = conflict.getSecondValue().split("/");
                    dc.persianToGregorian(
                            Integer.parseInt(splittedDate[0]),
                            Integer.parseInt(splittedDate[1]),
                            Integer.parseInt(splittedDate[2]));
                    person.setBirthDate(dc.getDate());
                    break;
                case BIRTH_PLACE:
                    person.setBirthPlace(conflict.getSecondValue());
                    break;
                default:
                    throw new InternalErrorException("The conflicted field is not defined in sabte ahval! value: " + conflict.getField() , null);
            }
        }
        personRepo.save(person);
        result.setUpdatedFields(comparisonResult.getConflicts());
        return result;
    }

    public PersonDescription addDescription(String uniqueCode, String description) {
        logger.info("Adding description for person with unique code {}. Text: {}", uniqueCode, description);

        PersonGeneralInfo generalInfo = prepareGeneralInfoObject(uniqueCode);

        PersonDescription desc = new PersonDescription();
        desc.setDescription(description);
        desc.setCreationDate(new Date());
        desc.setCreatorUsername(LocalThreadContext.getData().getUsername());
        desc.setPersonGeneralInfo(generalInfo);

        personDescriptionRepo.save(desc);

        return desc;
    }

    private PersonGeneralInfo prepareGeneralInfoObject(String uniqueCode) {

        if(personRepo.findByUniqueCode(uniqueCode).size() == 0)
            throw new EntityNotFoundException("Person with unique code " + uniqueCode + " was not found", null);

        PersonGeneralInfo generalInfo = null;

        Optional<PersonGeneralInfo> optGeneralInfo = personGeneralInfoRepo.findById(uniqueCode);

        if(optGeneralInfo.isPresent()) {
            generalInfo = optGeneralInfo.get();
        } else {
            generalInfo = new PersonGeneralInfo();
            generalInfo.setUniqueCode(uniqueCode);
            personGeneralInfoRepo.save(generalInfo);
        }

        return generalInfo;
    }

    public List<PersonDescription> getDescriptions(String uniqueCode) {
        return personDescriptionRepo.findBypersonGeneralInfoUniqueCodeOrderByCreationDateDesc(uniqueCode);
    }

    public void removeDescription(String uniqueCode, long descriptionId) {
        logger.info("Removing description with id {} for person with unique code {}", descriptionId, uniqueCode);
        Optional<PersonDescription> desc = personDescriptionRepo.findById(descriptionId);
        if(desc.isEmpty())
            throw new EntityNotFoundException(descriptionId, null);

        if(!desc.get().getPersonGeneralInfo().getUniqueCode().equals(uniqueCode))
            throw new InvalidInputException("given unique code doesn't match");

        if(!desc.get().getCreatorUsername().equals(LocalThreadContext.getData().getUsername()))
            throw new ForbiddenAccessException("Only creator of the description can remove it");

        personDescriptionRepo.delete(desc.get());

    }

    public void addPersonToBlackList(String uniqueCode, AddToBlackListReq req) {
        logger.info("Adding person with unique code {} to blacklist due to {}", uniqueCode, req.getReason());
        PersonGeneralInfo generalInfo = prepareGeneralInfoObject(uniqueCode);
        generalInfo.setAddedToBlackList(true);
        generalInfo.setBlackListReason(req.getReason());
        personGeneralInfoRepo.save(generalInfo);
        addDescription(uniqueCode, "به علت «" + req.getReason() + "» شخص در لیست سیاه قرار گرفت.");
    }

    public void removePersonFromBlackList(String uniqueCode, RemoveFromBlackListReq req) {
        logger.info("Removing person with unique code {} from blacklist due to {}", uniqueCode, req.getReason());
        PersonGeneralInfo generalInfo = prepareGeneralInfoObject(uniqueCode);
        if(!generalInfo.isAddedToBlackList())
            throw new InvalidInputException("Person with unique code " + uniqueCode + " is not in blacklist");
        generalInfo.setAddedToBlackList(false);
        generalInfo.setBlackListReason(req.getReason());
        personGeneralInfoRepo.save(generalInfo);
        addDescription(uniqueCode, "به علت «" + req.getReason() + "» شخص از لیست سیاه خارج شد.");
    }

    public List<Person> getPersonBlackList() {
        List<Person> result = new ArrayList<Person>();
        List<PersonGeneralInfo> generalInfos = personGeneralInfoRepo.findAllByAddedToBlackList(true);
        for(PersonGeneralInfo generalInfo: generalInfos) {
            Person person = personRepo.findByUniqueCodeAndCurrentVersion(generalInfo.getUniqueCode(), true);
            person.setGeneralInfo(generalInfo);
            result.add(person);
        }
        return result;
    }

    /**
     * creates new person if the given national code does not exist.
     * updates person
     * @param sabteAhvalObj
     */
    public void patchSabteAhvalValues(RealPerson sabteAhvalObj) {
        logger.info("patching sabte ahval values for real person with national code {}", sabteAhvalObj.getNationalCode());
        RealPerson person = findRealByNationalCode(sabteAhvalObj.getNationalCode());
        if(person == null)
            create(sabteAhvalObj);
        else {
            setSabteAhvalFieldsOnPerson(person, sabteAhvalObj);
            if(person.getStatus() == PersonStatus.CONFIRMED)
                create(person);
            else
                update(person.getId(), person);
        }
    }

    private void setSabteAhvalFieldsOnPerson(RealPerson person, RealPerson sabteAhvalObj) {
        person.setNationalCode(sabteAhvalObj.getNationalCode());
        person.setFirstName(sabteAhvalObj.getFirstName());
        person.setLastName(sabteAhvalObj.getLastName());
        person.setIdNumber(sabteAhvalObj.getIdNumber());
        person.setFatherName(sabteAhvalObj.getFatherName());
        person.setBirthPlace(sabteAhvalObj.getBirthPlace());
        person.setBirthDate(sabteAhvalObj.getBirthDate());
    }

    public void uploadPersonalImage(long personId, MultipartFile file) {
        logger.info("Uploading personal image with filename {} for person with id {}", file.getOriginalFilename(), personId);
        byte[] fileBytes = null;
        try {
            fileBytes = file.getBytes();
        } catch (Exception e) {
            throw new InternalErrorException("An exception occured while getting bytes of the uploaded file", e);
        }

        validatePersonalImage(file);

        Person person = findById(personId);
        if(person.getType() != PersonType.REAL)
            throw new InvalidInputException("Uploading personal image is only possible for REAL persons");
        if(person.getStatus() == PersonStatus.CONFIRMED)
            throw new UpdatingConfirmedPersonException();
        Document uploadedDocument = documentManagerClient.uploadBinary(fileBytes, file.getOriginalFilename());
        ((RealPerson)person).setPersonalImageDocumentId(uploadedDocument.getId());
        personRepo.save(person);
    }

    private void validatePersonalImage(MultipartFile file) {
        FileValidator.ValidationCriteria criteria = new FileValidator.ValidationCriteria();

        criteria.setMaxSizeInMegaBytes(2);
        criteria.addValidMimeType(MimeTypeEnum.IMAGE_PNG);
        criteria.addValidMimeType(MimeTypeEnum.IMAGE_JPG);
        criteria.addValidMimeType(MimeTypeEnum.IMAGE_JPEG);

        FileValidator.ValidationResult result;
        try {
            result = FileValidator.validate(file, criteria);
        } catch (IOException e) {
            throw new InternalErrorException("Failed to open file for validation", e);
        }
        if(!result.isValid())
            throw new InvalidInputException("uploaded personal image file is invalid. reason = " + result.getMessages());

    }

    public Document downloadPersonalImage(long personId) {
        logger.info("downloading personal image for person with id {}", personId);
        Person person = findById(personId);

        if(person.getType() != PersonType.REAL)
            throw new InvalidInputException("Downloading personal image is only possible for REAL persons");

        Long documentId = ((RealPerson)person).getPersonalImageDocumentId();

        if(documentId == null || documentId == 0)
            throw new EntityNotFoundException("Personal image is not uploaded for this person yet.", null);

        Document doc = documentManagerClient.get(documentId);
        byte[] file = documentManagerClient.download(documentId);
        doc.setBytes(file);
        return doc;
    }




}
